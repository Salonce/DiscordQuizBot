package dev.salonce.discordquizbot.application;

import dev.salonce.discordquizbot.util.messageSender;
import dev.salonce.discordquizbot.infrastructure.configs.QuizSetupConfig;
import dev.salonce.discordquizbot.domain.Match;
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
    private final messageSender messageSender;


    public void startMatch(MessageChannel messageChannel, String topic, int difficulty, Long userId) {

        if (matchService.matchExists(messageChannel.getId().asLong())) return;

        Match match = matchService.makeMatch(topic, difficulty, userId);
        matchService.put(messageChannel.getId().asLong(), match);

        Mono.firstWithSignal(runQuizFlow(messageChannel, match), runCheckIfAborted(messageChannel, match))
                .then(Mono.defer(() -> removeQuiz(messageChannel)))
                .subscribe();
    }

    private Mono<Message> runJoiningPhase(MessageChannel messageChannel, Match match) {
        int joinTimeout = quizSetupConfig.getTimeToJoinQuiz();

        return Mono.just(startingMessage.createSpec(match, joinTimeout))
            .flatMap(spec ->
                messageSender.send(messageChannel, spec)
                    .flatMap(message ->
                        Flux.interval(Duration.ofSeconds(1))
                            .take(joinTimeout)
                            .takeUntil(interval -> match.isStarting())
                            .concatMap(interval -> {
                                long timeLeft = joinTimeout - interval.intValue() - 1;
                                return messageSender.edit(message, startingMessage.editSpec(match, timeLeft));
                            })
                            .then(Mono.just(message)) // <-- return the message after countdown
                        )
            );
    }

   private Mono<Message> runCountdownPhase(Message message, Match match){
        int totalTimeToStart = quizSetupConfig.getTimeToStartMatch();
        match.startCountdownPhase();
        return Flux.interval(Duration.ofSeconds(1))
            .take(totalTimeToStart + 1)
            .concatMap(interval -> {
                Long timeLeft = (long) (totalTimeToStart - interval.intValue());
                return messageSender.edit(message, startingMessage.editSpec2(match, timeLeft));
            })
            .then(Mono.just(message));
    }

    private Mono<Void> runQuestionsPhase(MessageChannel messageChannel, Match match) {
        return Flux.generate(sink -> {
                    if (match.isFinished()) sink.complete();
                    else sink.next(match.getCurrentQuestion());
                })
                .takeWhile(question -> !match.isFinished())
                .concatMap(question -> runQuestionFlow(match, messageChannel))
                .then();
    }

    private Mono<Message> runResultsPhase(MessageChannel messageChannel, Match match){
        return messageSender.send(messageChannel, matchResultsMessage.createEmbed(match));
    }

    private Mono<Void> runQuizFlow(MessageChannel messageChannel, Match match){
        return runJoiningPhase(messageChannel, match)
            .flatMap(message -> runCountdownPhase(message, match))
            .flatMap(message -> runQuestionsPhase(messageChannel, match))
            .then(Mono.defer(() -> runResultsPhase(messageChannel, match)))
            .then();
    }

    private Mono<Void> runCheckIfAborted(MessageChannel messageChannel, Match match){
        return Flux.interval(Duration.ofMillis(500))
                .filter(tick -> match.isAborted())
                .next()
                .flatMap(tick -> messageSender.send(messageChannel, matchCanceledMessage.createEmbed(match)))
                .then();
    }

    private Mono<Void> removeQuiz(MessageChannel messageChannel){
        matchService.remove(messageChannel.getId().asLong());
        return Mono.empty();
    }

    private Mono<Void> runQuestionFlow(Match match, MessageChannel channel) {
        int totalTime = quizSetupConfig.getTimeToPickAnswer();
        int timeBetweenQuestions = quizSetupConfig.getTimeForNewQuestionToAppear();

        return Mono.just(questionMessage.createEmbed(match, totalTime))
                .flatMap(channel::createMessage)
                .flatMap(message -> {
                    match.startAnsweringPhase();
                    return emitUntilAllAnsweredOrTimeout(match, message, totalTime)
                            .then(Mono.defer(() -> messageSender.edit(message, questionMessage.editEmbedAfterAnswersWait(match))))
                            .then(Mono.delay(Duration.ofSeconds(1)))
                            .then(Mono.defer(() -> messageSender.edit(message, questionMessage.editEmbedWithScores(match))))
                            .then(Mono.fromRunnable(match::startBetweenQuestionsPhase))
                            .then(Mono.fromRunnable(match::checkInactivity))
                            .then(Mono.fromRunnable(match::nextQuestion))
                            .then(Mono.delay(Duration.ofSeconds(timeBetweenQuestions)))
                            .then(Mono.empty());
                });
    }
    private Mono<Void> emitUntilAllAnsweredOrTimeout(Match match, Message message, int totalTime) {
        return Flux.interval(Duration.ofSeconds(1))
                .take(totalTime)
                .takeUntil(tick -> match.everyoneAnswered())
                .flatMap(tick -> {
                    int timeLeft = totalTime - (tick.intValue() + 1);
                    return messageSender.edit(message, questionMessage.editEmbedWithTime(match, timeLeft));
                })
                .then();
    }
}