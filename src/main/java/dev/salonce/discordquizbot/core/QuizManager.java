package dev.salonce.discordquizbot.core;

import dev.salonce.discordquizbot.configs.TimersConfig;
import dev.salonce.discordquizbot.core.matches.Match;
import dev.salonce.discordquizbot.core.matches.MatchState;
import dev.salonce.discordquizbot.core.sendingmessages.MatchCanceledMessage;
import dev.salonce.discordquizbot.core.sendingmessages.MatchResultsMessage;
import dev.salonce.discordquizbot.core.sendingmessages.QuestionMessage;
import dev.salonce.discordquizbot.core.sendingmessages.StartingMessage;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class QuizManager {

    private final MatchStore matchStore;
    private final TimersConfig timersConfig;
    private final QuestionMessage questionMessage;
    private final StartingMessage startingMessage;
    private final MatchCanceledMessage matchCanceledMessage;
    private final MatchResultsMessage matchResultsMessage;

    public void addMatch(MessageChannel messageChannel, Match match) {
        int totalTimeToJoin = timersConfig.getTimeToJoinQuiz();
        int totalTimeToStart = timersConfig.getTimeToStartMatch();

        if (matchStore.containsKey(messageChannel)) {
            // send a message that a match is already in progress in that chat and can't start new one
            return;
        }

        matchStore.put(messageChannel, match);

        Mono<Void> normalFlow = startingMessage.create(messageChannel, totalTimeToJoin)
                .flatMap(message ->
                        Flux.interval(Duration.ofSeconds(1))
                                .take(totalTimeToJoin)
                                .takeUntil(interval -> match.getMatchState() == MatchState.COUNTDOWN)
                                .flatMap(interval -> {
                                    Long timeLeft = (long) (totalTimeToJoin - interval.intValue() - 1);
                                    return startingMessage.edit(message, messageChannel, timeLeft);
                                })
                                .then(Mono.just(message))
                )
                .flatMap(message -> closeEnrollment(message, match))
                .flatMap(message ->
                        Flux.interval(Duration.ofSeconds(1))
                                .take(totalTimeToStart + 1)
                                .flatMap(interval -> {
                                    Long timeLeft = (long) (totalTimeToStart - interval.intValue());
                                    return startingMessage.edit2(message, messageChannel, timeLeft);
                                })
                                .then(Mono.just(message))
                )
                .flatMap(message -> createQuestionMessages(messageChannel))
                .then(Mono.defer(() -> matchResultsMessage.create(messageChannel)))
                .then();

        Mono<Void> cancelFlow = Flux.interval(Duration.ofMillis(500))
                .filter(tick -> match.isClosed())
                .next()
                .flatMap(tick -> matchCanceledMessage.create(messageChannel))
                .then();

        Mono.firstWithSignal(normalFlow, cancelFlow)
                .then(Mono.defer(() -> {
                    matchStore.remove(messageChannel);
                    return Mono.empty();
                }))
                .subscribe();
    }

    private Mono<Void> createQuestionMessages(MessageChannel messageChannel) {
        Match match = matchStore.get(messageChannel);

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

        return questionMessage.create(messageChannel, index, totalTime)
                .flatMap(message -> {
                    openAnswering(messageChannel);
                    return createCountdownTimer(match, messageChannel, message, index, totalTime)
                            .then(Mono.defer(() -> questionMessage.editAfterAnswersWait(messageChannel, message, index)))
                            .then(Mono.delay(Duration.ofSeconds(1)))
                            .then(Mono.defer(() -> addPlayerPoints(messageChannel)))
                            .then(Mono.defer(() -> closeAnswering(messageChannel)))
                            .then(Mono.defer(() -> questionMessage.editWithScores(messageChannel, message, index)))
//                            .thenMany(Flux.interval(Duration.ofSeconds(1))
//                                    .take((long) totalTimeForNextQuestionToAppear)
//                                    .flatMap(tick -> {
//                                        Long timeLeft = (long) (totalTimeForNextQuestionToAppear - (tick.intValue() + 1));
//                                        return questionMessage.editWithScoresAndTimeLeft(messageChannel, message, index, timeLeft);
//                                    })
//                            )
                            .then(Mono.defer(() -> updateInactiveRoundsInARowCount(messageChannel)))
                            .then(Mono.defer(() -> switchStateToClosedIfInactiveRoundsInARowLimitReached(messageChannel)))
                            .then(Mono.delay(Duration.ofSeconds(timersConfig.getTimeForNewQuestionToAppear())))
                            .then(Mono.defer(() -> moveToNextQuestion(match)));
                });
    }
    private Mono<Void> createCountdownTimer(Match match, MessageChannel channel, Message message, long index, int totalTime) {
        return Flux.interval(Duration.ofSeconds(1))
                .take(totalTime)
                .takeUntil(tick -> match.everyoneAnswered())
                .flatMap(tick -> {
                    int timeLeft = totalTime - (tick.intValue() + 1);
                    return questionMessage.editWithTime(channel, message, index, timeLeft);
                })
                .then();
    }

    private Mono<Message> closeEnrollment(Message monoMessage, Match match){
        match.setMatchState(MatchState.COUNTDOWN);
        return Mono.just(monoMessage);
    }

    public Mono<Void> updateInactiveRoundsInARowCount(MessageChannel messageChannel){
        Match match = matchStore.get(messageChannel);
        match.updateInactiveRoundsInARowCount();
        return Mono.empty();
    }

    public Mono<Void> switchStateToClosedIfInactiveRoundsInARowLimitReached(MessageChannel messageChannel){
        Match match = matchStore.get(messageChannel);
        match.switchStateToClosedIfInactiveRoundsInARowLimitReached();
        return Mono.empty();
    }


    private Mono<Void> closeAnswering(MessageChannel messageChannel){
        Match match = matchStore.get(messageChannel);
        match.setMatchState(MatchState.WAITING);
        return Mono.empty();
    }

    private Mono<Void> openAnswering(MessageChannel messageChannel){
        Match match = matchStore.get(messageChannel);
        match.setMatchState(MatchState.ANSWERING);
        return Mono.empty();
    }

    private Mono<Void> moveToNextQuestion(Match match){
        match.skipToNextQuestion();
        return Mono.empty();
    }

    private Mono<Void> addPlayerPoints(MessageChannel messageChannel){
        matchStore.get(messageChannel).updateScores();
        return Mono.empty();
    }
}