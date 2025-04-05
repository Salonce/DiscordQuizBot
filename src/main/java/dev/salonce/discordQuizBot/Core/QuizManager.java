package dev.salonce.discordQuizBot.Core;

import dev.salonce.discordQuizBot.Configs.QuestionSetsConfig;
import dev.salonce.discordQuizBot.Buttons.AnswerInteractionEnum;
import dev.salonce.discordQuizBot.Buttons.ButtonInteraction;
import dev.salonce.discordQuizBot.Buttons.ButtonInteractionData;
import dev.salonce.discordQuizBot.Configs.QuizConfig;
import dev.salonce.discordQuizBot.Core.Matches.Match;
import dev.salonce.discordQuizBot.Core.Matches.EnumMatchClosed;
import dev.salonce.discordQuizBot.Core.MessagesSending.QuestionMessage;
import dev.salonce.discordQuizBot.Core.MessagesSending.StartingMessage;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizManager {

    private final Map<MessageChannel, Match> matches = new HashMap<>();
    private final QuizConfig quizConfig;
    private final QuestionSetsConfig questionSetsConfig;

    private QuestionMessage questionMessage = new QuestionMessage(matches);
    private StartingMessage startingMessage = new StartingMessage(matches);

    public QuizManager(QuestionSetsConfig questionSetsConfig, QuizConfig quizConfig){
        this.questionSetsConfig = questionSetsConfig;
        this.quizConfig = quizConfig;
    }

    public void addMatch(MessageChannel messageChannel, Match match) {
        int totalTimeToJoinLeft = quizConfig.getTimeToJoinQuiz();
        int totalTimeToStartLeft = quizConfig.getTimeToStartMatch();

        if (matches.containsKey(messageChannel)) {
            // send a message that a match is already in progress in that chat and can't start new one
            return;
        }

        matches.put(messageChannel, match);

        Mono<Void> normalFlow = startingMessage.create(messageChannel, totalTimeToJoinLeft)
                .flatMap(message ->
                        Flux.interval(Duration.ofSeconds(1))
                                .take(totalTimeToJoinLeft)
                                .takeUntil(interval -> match.isStartNow())
                                .flatMap(interval -> {
                                    Long timeLeft = (long) (totalTimeToJoinLeft - interval.intValue() - 1);
                                    return startingMessage.edit(message, messageChannel, timeLeft);
                                })
                                .then(Mono.just(message))
                )
                .flatMap(message -> closeEnrollment(message, match))
                .flatMap(message ->
                        Flux.interval(Duration.ofSeconds(1))
                                .take(totalTimeToStartLeft + 1)
                                .flatMap(interval -> {
                                    Long timeLeft = (long) (totalTimeToStartLeft - interval.intValue());
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
                .flatMap(tick -> createCanceledMatchMessage(messageChannel))
                .then();

        Mono.firstWithSignal(normalFlow, cancelFlow)
                .then(Mono.defer(() -> {
                    matches.remove(messageChannel);
                    return Mono.empty();
                }))
                .subscribe();
    }


    private Mono<Message> closeEnrollment(Message monoMessage, Match match){
        match.setEnrollment(false);
        System.out.println("enrollment closed");
        return Mono.just(monoMessage);
    }

    private Mono<Void> createQuestionMessagesSequentially(MessageChannel messageChannel) {
        Match match = matches.get(messageChannel);

        return Flux.generate(sink -> {
                    if (match.questionExists())
                        sink.next(match.getCurrentQuestion());
                    else
                        sink.complete();
                })
                .takeWhile(question -> !match.isClosed())
                .index()
                .concatMap(tuple -> {
                            long index = tuple.getT1();
                            return questionMessage.create(messageChannel, index, quizConfig.getTimeToPickAnswer())
                                    .flatMap(message -> {
                                        openAnswering(messageChannel);
                                        int totalTime = quizConfig.getTimeToPickAnswer();
                                        return Flux.interval(Duration.ofSeconds(1)) // Emit every second
                                                .take(totalTime)// Number of updates
                                                .takeUntil(interval -> match.isClosed() || match.everyoneAnswered())
                                                .flatMap(interval -> {
                                                    int timeLeft = totalTime - (interval.intValue() + 1); // Calculate remaining time
                                                    return questionMessage.editWithTime(messageChannel, message, index, timeLeft);
                                                })
                                                //.then(Mono.defer(() -> openAnswering(messageChannel)))
                                                //.then(Mono.delay(Duration.ofSeconds(totalTime)))
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
                )
                .then();
    }

    private Mono<Void> setNoAnswerCountAndCloseMatchIfLimit(MessageChannel messageChannel){
        Match match = matches.get(messageChannel);
        match.setNoAnswerCountAndCloseMatchIfLimit();
        return Mono.empty();
    }

    private Mono<Void> closeAnswering(MessageChannel messageChannel){
        Match match = matches.get(messageChannel);
        match.setAnsweringOpen(false);
        return Mono.empty();
    }

    private Mono<Void> openAnswering(MessageChannel messageChannel){
        Match match = matches.get(messageChannel);
        match.setAnsweringOpen(true);
        return Mono.empty();
    }

    private Mono<Void> createQuestionMessages(MessageChannel messageChannel) {
        return createQuestionMessagesSequentially(messageChannel); // Process all questions sequentially
                //.then(Mono.delay(Duration.ofSeconds(2))) // add time after last question?

    }

    private Mono<Void> moveToNextQuestion(Match match){
        match.skipToNextQuestion();
        return Mono.empty();
    }

    private Mono<Message> createMatchResultsMessage(MessageChannel messageChannel){
        Match match = matches.get(messageChannel);

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("Final scoreboard: " )
                .description(match.getFinalScoreboard())
                //.addField("\uD83C\uDFC6", "The winners are: " + match.getWinners(), false)
                .build();

        return messageChannel.createMessage(embed);
    }

    private Mono<Message> createCanceledMatchMessage(MessageChannel messageChannel){
        Match match = matches.get(messageChannel);
        String text = "Match has been closed.";
        if (match.getEnumMatchClosed() == EnumMatchClosed.BY_AUTOCLOSE)
            text = "Match has been autoclosed.";
        else if (match.getEnumMatchClosed() == EnumMatchClosed.BY_OWNER)
            text = "Match has been closed by the owner.";

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title(text )
                .build();

        return messageChannel.createMessage(embed);
    }

    public boolean cancelQuiz(ButtonInteraction buttonInteraction) {
        User user = buttonInteraction.getUser();
//      Message message = buttonInteraction.getMessage();
        MessageChannel messageChannel = buttonInteraction.getMessageChannel();
        Match match = matches.get(messageChannel);
        return match.closeMatch(user.getId().asLong());
    }

    public String addUserToMatch(ButtonInteraction buttonInteraction){
        Long userId = buttonInteraction.getUser().getId().asLong();
        Message message = buttonInteraction.getMessage();
        MessageChannel messageChannel = buttonInteraction.getMessageChannel();
        Match match = matches.get(messageChannel);
        int questionsNumber = match.getQuestions().size();

        if (matches.containsKey(messageChannel)){
            return matches.get(messageChannel).addPlayer(userId, questionsNumber);
        }
        else{
            return "This match doesn't exist anymore.";
        }
    }

    public String removeUserFromMatch(ButtonInteraction buttonInteraction){
        User user = buttonInteraction.getUser();
        Message message = buttonInteraction.getMessage();
        MessageChannel messageChannel = buttonInteraction.getMessageChannel();

        if (matches.containsKey(messageChannel))
            return matches.get(messageChannel).removePlayer(user.getId().asLong());
        else{
            return "This match doesn't exist anymore.";
        }
    }

    public String startNow(ButtonInteraction buttonInteraction){
        User user = buttonInteraction.getUser();
        Message message = buttonInteraction.getMessage();
        MessageChannel messageChannel = buttonInteraction.getMessageChannel();

        if (matches.containsKey(messageChannel)){
            if (!matches.get(messageChannel).isStartNow()) {
                matches.get(messageChannel).setStartNow(true);
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

        Match match = matches.get(messageChannel);

        if (match == null)
            return AnswerInteractionEnum.TOO_LATE; // could be different

        if (questionNum != match.getCurrentQuestionNum() || match.isAnsweringOpen() != true)
            return AnswerInteractionEnum.TOO_LATE;

        if (match.getPlayers().containsKey(userId)) {
            match.getPlayers().get(userId).getAnswersList().set(questionNum, answerNum);
            return AnswerInteractionEnum.VALID;
        }
        else return AnswerInteractionEnum.NOT_IN_MATCH;
    }

    public Mono<Void> addPlayerPoints(MessageChannel messageChannel){
        matches.get(messageChannel).updatePlayerPoints();
        return Mono.empty();
    }

    public Mono<Message> sendHelpMessage(MessageChannel messageChannel) {
        Match match = matches.get(messageChannel);
        String example = null;
        String example2 = null;
        Iterator <String> iterator = questionSetsConfig.getFiles().keySet().iterator();
        if (iterator.hasNext())
            example = iterator.next();
        if (iterator.hasNext())
            example2 = iterator.next();

        EmbedCreateSpec embed;
        if (example != null && example2 != null) {
            EmbedCreateSpec.Builder embedBuilder = EmbedCreateSpec.builder();

            List<String> categories = questionSetsConfig.getFiles().keySet().stream().sorted(String::compareTo).toList();

            embed = embedBuilder
                    .addField("How to start a quiz?", "Choose a category and type: **qq quiz <selected category>**", false)
                    .addField("Examples", "To start **" + example + "** quiz, type: **qq quiz " + example + "**\n" + "To start **" + example2 + "** quiz, type: **qq quiz " + example2 + "**", false)
                    .addField("Available categories", categories.stream().collect(Collectors.joining("\n")), false)
                    .build();
        }
        else{
            embed = EmbedCreateSpec.builder()
                    .title("No data" )
                    .addField("", "Sorry. This bot has no available quizzes.", false)
                    .build();
        }

        return messageChannel.createMessage(embed);
    }
}