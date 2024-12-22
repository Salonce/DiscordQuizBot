package dev.salonce.discordQuizBot.Core.Matches;

import dev.salonce.discordQuizBot.*;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizManager {

    private final Timers timers;

    private final Map<MessageChannel, Match> quizzes;
    private final QuestionsConfig questionsConfig;

    public QuizManager(QuestionsConfig questionsConfig, Timers timers){
        quizzes = new HashMap<>();
        this.questionsConfig = questionsConfig;
        this.timers = timers;
    }


    public void addMatch(MessageChannel messageChannel, Match match) {
        if (quizzes.containsKey(messageChannel)){
            //send message that starting a match is impossible because there is already one
        }
        else{
            quizzes.put(messageChannel, match);
            //System.out.println("Initial participants: " + match.getUserNames());
            createStartQuizMessage(messageChannel)
                    .delayElement(Duration.ofSeconds(timers.getTimeToJoinQuiz()))
                    .flatMap(message -> editStartQuizMessage2(message, messageChannel))
                    //.then(Mono.defer(() -> createStartingMatchMessage(messageChannel)))
                    .delayElement(Duration.ofSeconds(timers.getTimeForQuizToStart()))
                    .then(Mono.defer(() -> createQuestionMessages(messageChannel)))
                    .then(Mono.defer(() -> createMatchResultsMessage(messageChannel)))
                    .then(Mono.defer(() -> Mono.just(quizzes.remove(messageChannel))))
                    .subscribe();
        }
    }

    private Mono<Void> createQuestionMessagesSequentially(MessageChannel messageChannel) {
        Match match = quizzes.get(messageChannel);

        return Flux.generate(sink -> {
                    if (match.questionExists())
                        sink.next(match.getQuestion());
                    else
                        sink.complete();
                })
                .index()
                .concatMap(tuple -> {
                            long index = tuple.getT1();
                    return createQuestionMessage(messageChannel, index, timers.getTimeToAnswerQuestion())
                            .flatMap(message -> {
                                openAnswering(messageChannel);
                                int totalTime = timers.getTimeToAnswerQuestion();
                                return Flux.interval(Duration.ofSeconds(1)) // Emit every 5 seconds
                                        .take(totalTime - 1) // Number of updates
                                        .flatMap(interval -> {
                                            int timeLeft = totalTime - interval.intValue(); // Calculate remaining time
                                            return editQuestionMessageTime(messageChannel, message, index, timeLeft);
                                        })
                                        //.then(Mono.defer(() -> openAnswering(messageChannel)))
                                        //.then(Mono.delay(Duration.ofSeconds(totalTime)))
                                        .then(Mono.defer(() -> editQuestionMessageInitial(messageChannel, message, index)))
                                        .then(Mono.delay(Duration.ofSeconds(1)))
                                        .then(Mono.defer(() -> addPlayerPoints(messageChannel)))
                                        .then(Mono.defer(() -> closeAnswering(messageChannel)))
                                        .then(Mono.defer(() -> editQuestionMessage(messageChannel, message, index)))
                                        .then(Mono.delay(Duration.ofSeconds(timers.getTimeForNewQuestionToAppear())))
                                        .then(Mono.defer(() -> moveToNextQuestion(match)));
                            });
                        }
                )
                .then();
    }

    private Mono<Void> closeAnswering(MessageChannel messageChannel){
        Match match = quizzes.get(messageChannel);
        match.setAnsweringOpen(false);
        return Mono.empty();
    }

    private Mono<Void> openAnswering(MessageChannel messageChannel){
        Match match = quizzes.get(messageChannel);
        match.setAnsweringOpen(true);
        return Mono.empty();
    }


    private Mono<Message> createQuestionMessage(MessageChannel messageChannel, Long questionNumber, int timeLeft){
        Match match = quizzes.get(messageChannel);
        String questionsAnswers = match.getQuestion().getStringAnswers(false);
        int answersSize = match.getQuestion().getAnswers().size();

        List<Button> buttons = new ArrayList<>();
        for (int i = 0; i < answersSize; i++) {
            buttons.add(Button.success("Answer-" + (char)('A' + i) + "-" + questionNumber.toString(), String.valueOf((char)('A' + i))));
            //System.out.println("Creating button of id:" + "Answer-" + (char)('A' + i) + "-" + questionNumber.toString());
        }

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("#" + (match.getQuestionNumber() + 1) + " **" + match.getQuestion().getQuestion() + "**")
                //.description("**" + match.getQuestion().getQuestion() + "**")
                .addField("\n", questionsAnswers + "\n", false)
                .addField("\n", "You have fewer than " + timeLeft + "s to answer." + "\n", false)
                .build();

        MessageCreateSpec spec = MessageCreateSpec.builder()
                .addComponent(ActionRow.of(buttons))
                .addEmbed(embed)
                .build();

        return messageChannel.createMessage(spec);
    }

    private Mono<Message> editQuestionMessageTime(MessageChannel messageChannel, Message message, Long questionNumber, int timeLeft){
        Match match = quizzes.get(messageChannel);
        String questionsAnswers = match.getQuestion().getStringAnswers(false);
        int answersSize = match.getQuestion().getAnswers().size();

        List<Button> buttons = new ArrayList<>();
        for (int i = 0; i < answersSize; i++) {
            buttons.add(Button.success("Answer-" + (char)('A' + i) + "-" + questionNumber.toString(), String.valueOf((char)('A' + i))));
            //System.out.println("Creating button of id:" + "Answer-" + (char)('A' + i) + "-" + questionNumber.toString());
        }

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("#" + (match.getQuestionNumber() + 1) + " **" + match.getQuestion().getQuestion() + "**")
                //.description("**" + match.getQuestion().getQuestion() + "**")
                .addField("\n", questionsAnswers + "\n", false)
                .addField("\n", "You have fewer than " + timeLeft + "s to answer." + "\n", false)
                .build();

        return message.edit(MessageEditSpec.builder()
                .addComponent(ActionRow.of(buttons))
                .addEmbed(embed)
                .build());
    }

    private Mono<Message> editQuestionMessageInitial(MessageChannel messageChannel, Message message, Long questionNumber){
        Match match = quizzes.get(messageChannel);
        String questionsAnswers = match.getQuestion().getStringAnswers(false);
        int answersSize = match.getQuestion().getAnswers().size();

        List<Button> buttons = new ArrayList<>();
        for (int i = 0; i < answersSize; i++) {
            buttons.add(Button.success("Answer-" + (char)('A' + i) + "-" + questionNumber.toString(), String.valueOf((char)('A' + i))).disabled());
            //System.out.println("Creating button of id:" + "Answer-" + (char)('A' + i) + "-" + questionNumber.toString());
        }

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("#" + (match.getQuestionNumber() + 1) + " **" + match.getQuestion().getQuestion() + "**")
                //.description("**" + match.getQuestion().getQuestion() + "**")
                .addField("\n", questionsAnswers + "\n", false)
                .build();

        return message.edit(MessageEditSpec.builder()
                .addComponent(ActionRow.of(buttons))
                .addEmbed(embed)
                .build());
    }


    private Mono<Message> editQuestionMessage(MessageChannel messageChannel, Message message, Long questionNumber){
        Match match = quizzes.get(messageChannel);
        String questionsAnswers = match.getQuestion().getStringAnswers(true);
        int answersSize = match.getQuestion().getAnswers().size();

        List<Button> buttons = new ArrayList<>();
        for (int i = 0; i < answersSize; i++) {
            buttons.add(Button.success("Answer-" + (char)('A' + i) + "-" + questionNumber.toString(), String.valueOf((char)('A' + i))).disabled());
            //System.out.println("Creating button of id:" + "Answer-" + (char)('A' + i) + "-" + questionNumber.toString());
        }

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("#" + (match.getQuestionNumber() + 1) + " **" + match.getQuestion().getQuestion() + "**")
                .addField("\n", questionsAnswers + "\n", false)
                .addField("Explanation", match.getQuestion().getExplanation() + "\n", false)
                //.addField("", "Answers:\n" + match.getUsersAnswers(), false)
                .addField("Answers", match.getUsersAnswers(), false)
                .addField("Scoreboard", match.getScoreboard(), false)
                .build();

        return message.edit(MessageEditSpec.builder()
                .addComponent(ActionRow.of(buttons))
                .addEmbed(embed)
                .build());
    }

    public Mono<Message> createStartQuizMessage(MessageChannel messageChannel){
        Match match = quizzes.get(messageChannel);
        int timeToJoinQuizRefreshed = timers.getTimeToJoinQuiz();

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                //.title("\uD83C\uDFC1 Java Quiz")
                .title("\uD83C\uDFC1" + match.getName() + " quiz")
                .addField("", "Questions: " + match.getQuestions().size(), false)
                .addField("", "Participants: " + match.getUserNames(), false)
                .addField("", "You have " + timeToJoinQuizRefreshed + " seconds to join.", false)
                .build();

        MessageCreateSpec spec = MessageCreateSpec.builder()
                .addComponent(ActionRow.of(Button.success("joinQuiz", "Join"), Button.success("leaveQuiz", "Leave")))
                .addEmbed(embed)
                .build();

        return messageChannel.createMessage(spec);
    }

    private Mono<Message> editStartQuizMessage(Message message, MessageChannel messageChannel){
        Match match = quizzes.get(messageChannel);
        int timeToJoinQuizRefreshed = timers.getTimeToJoinQuiz();

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                //.title("\uD83C\uDFC1 Java Quiz")
                .title("\uD83C\uDFC1" + match.getName() + " quiz")
                .addField("", "Questions: " + match.getQuestions().size(), false)
                .addField("", "Participants: " + match.getUserNames(), false)
                .addField("", "You have " + timeToJoinQuizRefreshed + " seconds to join.", false)
                .build();

        return message.edit(MessageEditSpec.builder()
                .addComponent(ActionRow.of(Button.success("joinQuiz", "Join"), Button.success("leaveQuiz", "Leave")))
                .addEmbed(embed)
                .build());
    }

    private Mono<Message> editStartQuizMessage2(Message message, MessageChannel messageChannel){
        Match match = quizzes.get(messageChannel);
        int timeToJoinQuizRefreshed = timers.getTimeToJoinQuiz();

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                //.title("\uD83C\uDFC1 Java Quiz")
                .title("\uD83C\uDFC1" + match.getName() + " quiz")
                .addField("", "Questions: " + match.getQuestions().size(), false)
                .addField("", "Participants: " + match.getUserNames(), false)
                .addField("", "Starting in " + timeToJoinQuizRefreshed + " seconds.", false)
                .build();

        return message.edit(MessageEditSpec.builder()
                .addComponent(ActionRow.of(Button.success("joinQuiz", "Join").disabled(), Button.success("leaveQuiz", "Leave").disabled()))
                .addEmbed(embed)
                .build());
    }

    private Mono<Void> createQuestionMessages(MessageChannel messageChannel) {
        return createQuestionMessagesSequentially(messageChannel) // Process all questions sequentially
                //.then(Mono.delay(Duration.ofSeconds(2))) // add time after last question?
                .then();
    }

    private Mono<Void> moveToNextQuestion(Match match){
        match.nextQuestion();
        return Mono.empty();
    }

    private Mono<Message> createMatchResultsMessage(MessageChannel messageChannel){
        Match match = quizzes.get(messageChannel);

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("Final scoreboard: " )
                .description(match.getScoreboard())
                .addField("", "The winners are: " + match.getWinners(), false)
                .build();

        return messageChannel.createMessage(embed);
    }

    public void addUserToMatch(ButtonInteraction buttonInteraction){
        User user = buttonInteraction.getUser();
        Message message = buttonInteraction.getMessage();
        MessageChannel messageChannel = buttonInteraction.getMessageChannel();
        Match match = quizzes.get(messageChannel);
        int questionsNumber = match.getQuestions().size();

        if (quizzes.containsKey(messageChannel)){
            if (quizzes.get(messageChannel).addPlayer(user, questionsNumber)) {
                //System.out.println("Participants: " + quizzes.get(messageChannel).getUserNames());
                editStartQuizMessage(message, messageChannel).subscribe();
            }
        }
    }

    public void removeUserFromMatch(ButtonInteraction buttonInteraction){
        User user = buttonInteraction.getUser();
        Message message = buttonInteraction.getMessage();
        MessageChannel messageChannel = buttonInteraction.getMessageChannel();

        if (quizzes.containsKey(messageChannel)){
            if (quizzes.get(messageChannel).removePlayer(user)) {
                editStartQuizMessage(message, messageChannel).subscribe();
                //messageChannel.createMessage()
                //change message to the message channel that user is removed
            }
            else{
                //change message to the message channel that interaction failed because the match doesn't exist
            }
        }
    }

    public AnswerInteractionEnum setPlayerAnswer(ButtonInteraction buttonInteraction, ButtonInteractionData buttonInteractionData){
        User user = buttonInteraction.getUser();
        MessageChannel messageChannel = buttonInteraction.getMessageChannel();
        int questionNum = buttonInteractionData.getQuestionNumber();
        int answerNum = buttonInteractionData.getAnswerNumber();

        Match match = quizzes.get(messageChannel);

        if (match == null)
            return AnswerInteractionEnum.TOO_LATE; // could be different

        if (questionNum != match.getQuestionNumber() || match.isAnsweringOpen() != true)
            return AnswerInteractionEnum.TOO_LATE;

        if (match.getPlayers().containsKey(user)) {
            match.getPlayers().get(user).setCurrentAnswerNum(questionNum);
            match.getPlayers().get(user).getAnswersList().set(questionNum, answerNum);
            return AnswerInteractionEnum.VALID;
        }
        else return AnswerInteractionEnum.NOT_IN_MATCH;
    }

    public Mono<Void> addPlayerPoints(MessageChannel messageChannel){
        quizzes.get(messageChannel).addPlayerPoints();
        return Mono.empty();
    }

    public Mono<Message> sendHelpMessage(MessageChannel messageChannel) {
        Match match = quizzes.get(messageChannel);

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("Help" )
                .addField("Categories", questionsConfig.getFiles().keySet().stream().sorted(String::compareTo).collect(Collectors.joining(", ")), false)
                //.addField("Syntax", "**qq quiz *category***", false)
                .addField("Example", "To start memory quiz, type: **qq quiz *memory***.", false)
                .build();

        return messageChannel.createMessage(embed);
    }
}