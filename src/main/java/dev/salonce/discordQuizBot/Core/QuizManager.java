package dev.salonce.discordQuizBot.Core;

import dev.salonce.discordQuizBot.Configs.QuestionSetsConfig;
import dev.salonce.discordQuizBot.Buttons.AnswerInteractionEnum;
import dev.salonce.discordQuizBot.Buttons.ButtonInteraction;
import dev.salonce.discordQuizBot.Buttons.ButtonInteractionData;
import dev.salonce.discordQuizBot.Configs.QuizConfig;
import dev.salonce.discordQuizBot.Core.Matches.Match;
import dev.salonce.discordQuizBot.Core.Matches.EnumMatchClosed;
import dev.salonce.discordQuizBot.Core.MessagesSending.HelpMessage;
import dev.salonce.discordQuizBot.Core.MessagesSending.MatchCanceledMessage;
import dev.salonce.discordQuizBot.Core.MessagesSending.QuestionMessage;
import dev.salonce.discordQuizBot.Core.MessagesSending.StartingMessage;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class QuizManager {

    private final MatchStore matchStore;
    private final QuizConfig quizConfig;
    private final QuestionSetsConfig questionSetsConfig;
    private final QuestionMessage questionMessage;
    private final StartingMessage startingMessage;
    private final MatchCanceledMessage matchCanceledMessage;

    public void addMatch(MessageChannel messageChannel, Match match) {
        int totalTimeToJoin = quizConfig.getTimeToJoinQuiz();
        int totalTimeToStart = quizConfig.getTimeToStartMatch();

        if (matchStore.containsKey(messageChannel)) {
            // send a message that a match is already in progress in that chat and can't start new one
            return;
        }

        matchStore.put(messageChannel, match);

        Mono<Void> normalFlow = startingMessage.create(messageChannel, totalTimeToJoin)
                .flatMap(message ->
                        Flux.interval(Duration.ofSeconds(1))
                                .take(totalTimeToJoin)
                                .takeUntil(interval -> match.isStartNow())
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
                .then(Mono.defer(() -> createMatchResultsMessage(messageChannel)))
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
        int totalTime = quizConfig.getTimeToPickAnswer();

        return questionMessage.create(messageChannel, index, totalTime)
                .flatMap(message -> {
                    openAnswering(messageChannel);
                    return createCountdownTimer(match, messageChannel, message, index, totalTime)
                            .then(Mono.defer(() -> questionMessage.editFirst(messageChannel, message, index)))
                            .then(Mono.delay(Duration.ofSeconds(1)))
                            .then(Mono.defer(() -> addPlayerPoints(messageChannel)))
                            .then(Mono.defer(() -> closeAnswering(messageChannel)))
                            .then(Mono.defer(() -> questionMessage.editWithScores(messageChannel, message, index)))
                            .then(Mono.defer(() -> setNoAnswerCountAndCloseMatchIfLimit(messageChannel)))
                            .then(Mono.delay(Duration.ofSeconds(quizConfig.getTimeForNewQuestionToAppear())))
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
        match.setEnrollment(false);
        System.out.println("enrollment closed");
        return Mono.just(monoMessage);
    }

    private Mono<Void> setNoAnswerCountAndCloseMatchIfLimit(MessageChannel messageChannel){
        Match match = matchStore.get(messageChannel);
        match.setNoAnswerCountAndCloseMatchIfLimit();
        return Mono.empty();
    }

    private Mono<Void> closeAnswering(MessageChannel messageChannel){
        Match match = matchStore.get(messageChannel);
        match.setAnsweringOpen(false);
        return Mono.empty();
    }

    private Mono<Void> openAnswering(MessageChannel messageChannel){
        Match match = matchStore.get(messageChannel);
        match.setAnsweringOpen(true);
        return Mono.empty();
    }

    private Mono<Void> moveToNextQuestion(Match match){
        match.skipToNextQuestion();
        return Mono.empty();
    }

    private Mono<Message> createMatchResultsMessage(MessageChannel messageChannel){
        Match match = matchStore.get(messageChannel);

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("Final scoreboard: " )
                .description(match.getFinalScoreboard())
                //.addField("\uD83C\uDFC6", "The winners are: " + match.getWinners(), false)
                .build();

        return messageChannel.createMessage(embed);
    }

    public boolean cancelQuiz(ButtonInteraction buttonInteraction) {
        User user = buttonInteraction.getUser();
//      Message message = buttonInteraction.getMessage();
        MessageChannel messageChannel = buttonInteraction.getMessageChannel();
        Match match = matchStore.get(messageChannel);
        return match.closeMatch(user.getId().asLong());
    }

    public String addUserToMatch(ButtonInteraction buttonInteraction){
        Long userId = buttonInteraction.getUser().getId().asLong();
        Message message = buttonInteraction.getMessage();
        MessageChannel messageChannel = buttonInteraction.getMessageChannel();
        Match match = matchStore.get(messageChannel);
        int questionsNumber = match.getQuestions().size();

        if (matchStore.containsKey(messageChannel)){
            return matchStore.get(messageChannel).addPlayer(userId, questionsNumber);
        }
        else{
            return "This match doesn't exist anymore.";
        }
    }

    public String removeUserFromMatch(ButtonInteraction buttonInteraction){
        User user = buttonInteraction.getUser();
        Message message = buttonInteraction.getMessage();
        MessageChannel messageChannel = buttonInteraction.getMessageChannel();

        if (matchStore.containsKey(messageChannel))
            return matchStore.get(messageChannel).removePlayer(user.getId().asLong());
        else{
            return "This match doesn't exist anymore.";
        }
    }

    public String startNow(ButtonInteraction buttonInteraction){
        User user = buttonInteraction.getUser();
        Message message = buttonInteraction.getMessage();
        MessageChannel messageChannel = buttonInteraction.getMessageChannel();

        if (matchStore.containsKey(messageChannel)){
            if (!matchStore.get(messageChannel).isStartNow()) {
                matchStore.get(messageChannel).setStartNow(true);
                return "Starting immediately";
            }
            else
                return "Already started";
        }
        else{
            return "This match doesn't exist anymore.";
        }
    }

    public AnswerInteractionEnum setPlayerAnswer(ButtonInteraction buttonInteraction, ButtonInteractionData buttonInteractionData){
        Long userId = buttonInteraction.getUser().getId().asLong();
        MessageChannel messageChannel = buttonInteraction.getMessageChannel();
        int questionNum = buttonInteractionData.getQuestionNumber();
        int answerNum = buttonInteractionData.getAnswerNumber();

        Match match = matchStore.get(messageChannel);

        if (match == null)
            return AnswerInteractionEnum.TOO_LATE; // could be different

        if (questionNum != match.getCurrentQuestionNum() || !match.isAnsweringOpen())
            return AnswerInteractionEnum.TOO_LATE;

        if (match.getPlayers().containsKey(userId)) {
            match.getPlayers().get(userId).getAnswersList().set(questionNum, answerNum);
            return AnswerInteractionEnum.VALID;
        }
        else return AnswerInteractionEnum.NOT_IN_MATCH;
    }

    public Mono<Void> addPlayerPoints(MessageChannel messageChannel){
        matchStore.get(messageChannel).updatePlayerPoints();
        return Mono.empty();
    }
}