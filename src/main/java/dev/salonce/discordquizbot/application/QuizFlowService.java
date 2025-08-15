package dev.salonce.discordquizbot.application;

import dev.salonce.discordquizbot.infrastructure.configs.TimersConfig;
import dev.salonce.discordquizbot.domain.Match;
import dev.salonce.discordquizbot.domain.MatchState;
import dev.salonce.discordquizbot.infrastructure.messages.out.MatchCanceledMessage;
import dev.salonce.discordquizbot.infrastructure.messages.out.MatchResultsMessage;
import dev.salonce.discordquizbot.infrastructure.messages.out.QuestionMessage;
import dev.salonce.discordquizbot.infrastructure.messages.out.StartingMessage;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageEditSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class QuizFlowService {

    private final MatchService matchService;
    private final TimersConfig timersConfig;
    private final QuestionMessage questionMessage;
    private final StartingMessage startingMessage;
    private final MatchCanceledMessage matchCanceledMessage;
    private final MatchResultsMessage matchResultsMessage;

    public void addMatch(MessageChannel messageChannel, Match match) {
        int totalTimeToJoin = timersConfig.getTimeToJoinQuiz();
        int totalTimeToStart = timersConfig.getTimeToStartMatch();

        if (matchService.containsKey(messageChannel.getId().asLong())) {
            // send a message that a match is already in progress in that chat and can't start new one
            return;
        }

        matchService.put(messageChannel.getId().asLong(), match);

        Mono<Void> normalFlow = Mono.just(startingMessage.createSpec(match, totalTimeToJoin))
                .flatMap(messageChannel::createMessage)
                .flatMap(message ->
                        Flux.interval(Duration.ofSeconds(1))
                                .take(totalTimeToJoin)
                                .takeUntil(interval -> match.getMatchState() == MatchState.COUNTDOWN)
                                .flatMap(interval -> {
                                    Long timeLeft = (long) (totalTimeToJoin - interval.intValue() - 1);
                                    MessageEditSpec spec = startingMessage.editSpec(match, timeLeft);
                                    return message.edit(spec);
                                })
                                .then(Mono.just(message))
                )
                .map(message -> {
                    match.startCountdownPhase();
                    return message;
                })
                .flatMap(message ->
                        Flux.interval(Duration.ofSeconds(1))
                                .take(totalTimeToStart + 1)
                                .flatMap(interval -> {
                                    Long timeLeft = (long) (totalTimeToStart - interval.intValue());
                                    MessageEditSpec spec = startingMessage.editSpec2(match, timeLeft);
                                    return message.edit(spec);
                                })
                                .then(Mono.just(message))
                )
                .flatMap(message -> {
                    messageChannel.getId();
                    return createQuestionMessages(messageChannel);
                })
                .then(Mono.defer(() -> {
                    EmbedCreateSpec embed = matchResultsMessage.createEmbed(match);
                    return messageChannel.createMessage(embed);})
                )
                .then();

        Mono<Void> cancelFlow = Flux.interval(Duration.ofMillis(500))
                .filter(tick -> match.isClosed())
                .next()
                .flatMap(tick -> {
                    EmbedCreateSpec embed = matchCanceledMessage.createEmbed(match);
                    return messageChannel.createMessage(embed);}
                )
                .then();

        Mono.firstWithSignal(normalFlow, cancelFlow)
                .then(Mono.defer(() -> {
                    matchService.remove(messageChannel.getId().asLong());
                    return Mono.empty();
                }))
                .subscribe();
    }

    private Mono<Void> createQuestionMessages(MessageChannel messageChannel) {
        Match match = matchService.get(messageChannel.getId().asLong());

        return Flux.generate(sink -> {
                    if (match.questionExists()) {
                        sink.next(match.getCurrentQuestion());
                    } else {
                        sink.complete();
                    }
                })
                .takeWhile(question -> !match.isClosed())
                .index()
                .concatMap(tuple -> {
                    long index = tuple.getT1();
                    return handleSingleQuestion(match, messageChannel, index);
                })
                .then();
    }

    private Mono<Void> handleSingleQuestion(Match match, MessageChannel messageChannel, long index) {
        int totalTime = timersConfig.getTimeToPickAnswer();
        int totalTimeForNextQuestionToAppear = timersConfig.getTimeForNewQuestionToAppear();

        return Mono.just(questionMessage.createEmbed(match, index, totalTime))
                .flatMap(messageChannel::createMessage)
                .flatMap(message -> {
                    match.startAnsweringPhase();
                    return createCountdownTimer(match, messageChannel, message, index, totalTime)
                            .then(Mono.defer(() -> {
                                MessageEditSpec spec = questionMessage.editEmbedAfterAnswersWait(match, index);
                                return message.edit(spec);
                            }))
                            .then(Mono.delay(Duration.ofSeconds(1)))
                            .then(Mono.defer(() -> {match.updateScores(); return Mono.empty();}))
                            .then(Mono.defer(() -> {match.startWaitingPhase(); return Mono.empty();}))
                            .then(Mono.defer(() -> {
                                MessageEditSpec spec = questionMessage.editEmbedWithScores(match, index);
                                return message.edit(spec);
                            }))
                            .then(Mono.defer(() -> {match.updateInactiveRounds(); return Mono.empty();}))
                            .then(Mono.defer(() -> {match.closeIfInactiveLimitReached(); return Mono.empty();}))
                            .then(Mono.delay(Duration.ofSeconds(timersConfig.getTimeForNewQuestionToAppear())))
                            .then(Mono.defer(() -> {match.skipToNextQuestion(); return Mono.empty();}));
                });
    }
    private Mono<Void> createCountdownTimer(Match match, MessageChannel channel, Message message, long index, int totalTime) {
        return Flux.interval(Duration.ofSeconds(1))
                .take(totalTime)
                .takeUntil(tick -> match.everyoneAnswered())
                .flatMap(tick -> {
                    int timeLeft = totalTime - (tick.intValue() + 1);
                    MessageEditSpec spec = questionMessage.editEmbedWithTime(match, index, timeLeft);
                    return message.edit(spec);
                })
                .then();
    }
}