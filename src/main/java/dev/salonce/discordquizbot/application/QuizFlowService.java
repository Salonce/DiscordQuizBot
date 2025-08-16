package dev.salonce.discordquizbot.application;

import dev.salonce.discordquizbot.infrastructure.DiscordMessageSender;
import dev.salonce.discordquizbot.infrastructure.configs.QuizSetupConfig;
import dev.salonce.discordquizbot.domain.Match;
import dev.salonce.discordquizbot.domain.MatchState;
import dev.salonce.discordquizbot.infrastructure.messages.out.MatchCanceledMessage;
import dev.salonce.discordquizbot.infrastructure.messages.out.MatchResultsMessage;
import dev.salonce.discordquizbot.infrastructure.messages.out.QuestionMessage;
import dev.salonce.discordquizbot.infrastructure.messages.out.StartingMessage;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class QuizFlowService {

    private final MatchService matchService;
    private final QuizSetupConfig quizSetupConfig;
    private final QuestionMessage questionMessage;
    private final StartingMessage startingMessage;
    private final MatchCanceledMessage matchCanceledMessage;
    private final MatchResultsMessage matchResultsMessage;
    private final DiscordMessageSender discordMessageSender;

    public void addMatch(MessageChannel messageChannel, Match match) {
        int totalTimeToJoin = quizSetupConfig.getTimeToJoinQuiz();
        int totalTimeToStart = quizSetupConfig.getTimeToStartMatch();

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
                                    return discordMessageSender.edit(message, startingMessage.editSpec(match, timeLeft));
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
                                    return discordMessageSender.edit(message, startingMessage.editSpec2(match, timeLeft));
                                })
                                .then(Mono.just(message))
                )
                .flatMap(message -> {
                    messageChannel.getId();
                    return createQuestionMessages(messageChannel);
                })
                .then(Mono.defer(() -> discordMessageSender.send(messageChannel, matchResultsMessage.createEmbed(match))))
                .then();

        Mono<Void> cancelFlow = Flux.interval(Duration.ofMillis(500))
                .filter(tick -> match.isClosed())
                .next()
                .flatMap(tick -> discordMessageSender.send(messageChannel, matchCanceledMessage.createEmbed(match)))
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
                    return runQuestionFlow(match, messageChannel, index);
                })
                .then();
    }

    private Mono<Void> runQuestionFlow(Match match, MessageChannel messageChannel, long index) {
        int totalTime = quizSetupConfig.getTimeToPickAnswer();
        int timeForNextQuestionToAppear = quizSetupConfig.getTimeForNewQuestionToAppear();

        return Mono.just(questionMessage.createEmbed(match, index, totalTime))
                .flatMap(messageChannel::createMessage)
                .flatMap(message -> {
                    match.startAnsweringPhase();
                    return createCountdownTimer(match, message, index, totalTime)
                            .then(Mono.defer(() -> discordMessageSender.edit(message, questionMessage.editEmbedAfterAnswersWait(match, index))))
                            .then(Mono.delay(Duration.ofSeconds(1)))
                            .then(Mono.fromRunnable(match::updateScores))
                            .then(Mono.fromRunnable(match::startWaitingPhase))
                            .then(Mono.defer(() -> discordMessageSender.edit(message, questionMessage.editEmbedWithScores(match, index))))
                            .then(Mono.fromRunnable(match::updateInactiveRounds))
                            .then(Mono.fromRunnable(match::closeIfInactiveLimitReached))
                            .then(Mono.delay(Duration.ofSeconds(timeForNextQuestionToAppear)))
                            .then(Mono.fromRunnable(match::skipToNextQuestion));
                });
    }
    private Mono<Void> createCountdownTimer(Match match, Message message, long index, int totalTime) {
        return Flux.interval(Duration.ofSeconds(1))
                .take(totalTime)
                .takeUntil(tick -> match.everyoneAnswered())
                .flatMap(tick -> {
                    int timeLeft = totalTime - (tick.intValue() + 1);
                    return discordMessageSender.edit(message, questionMessage.editEmbedWithTime(match, index, timeLeft));
                })
                .then();
    }
}