package dev.salonce.discordQuizBot.Core.Matches;

import dev.salonce.discordQuizBot.AnswerInteractionEnum;
import dev.salonce.discordQuizBot.ButtonInteraction;
import dev.salonce.discordQuizBot.ButtonInteractionData;
import dev.salonce.discordQuizBot.Core.Messages.MessageSender;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;
import discord4j.rest.util.Color;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuizManager {

    private final int enrollmentTime = 5; // testing is 5, default is 60
    private final int preparationTime = 5; // testing is 5, default is 10
    private final int participationTimeWait = 5;
    private final Map<MessageChannel, Match> quizzes;

    @Autowired
    private MessageSender messageSender;

    public QuizManager(){
        quizzes = new HashMap<>();
    }


    public void addMatch(MessageChannel messageChannel, Match match) {
        if (quizzes.containsKey(messageChannel)){
            //send message that starting a match is impossible because there is already one
        }
        else{
            quizzes.put(messageChannel, match);
            //System.out.println("Initial participants: " + match.getUserNames());
            createStartQuizMessage(messageChannel)
                    .delayElement(Duration.ofSeconds(enrollmentTime))
                    .flatMap(message -> editStartQuizMessage2(message, messageChannel))
                    //.then(Mono.defer(() -> createStartingMatchMessage(messageChannel)))
                    .delayElement(Duration.ofSeconds(preparationTime))
                    .then(Mono.defer(() -> createQuestionMessages(messageChannel)))
                    .then(Mono.defer(() -> createMatchResultsMessage(messageChannel)))
                    .then(Mono.defer(() -> Mono.just(quizzes.remove(messageChannel))))
                    .subscribe();
        }
    }

    private Mono<Void> createQuestionMessagesSequentially(MessageChannel messageChannel) {
        Match match = quizzes.get(messageChannel);
        int newQuestionWait = 10; //default is 10, test is 5
        int AnswerTimeWait = 20; //default is 30, test is 5

        return Flux.generate(sink -> {
                    if (match.questionExists())
                        sink.next(match.getQuestion());
                    else
                        sink.complete();
                })
                .index()
                .concatMap(tuple -> {
                            long index = tuple.getT1();
                    return createQuestionMessage(messageChannel, index)
                            .flatMap(message -> Mono.just(message)
                                    .then(Mono.defer(() -> openAnswering(messageChannel)))
                                    .then(Mono.delay(Duration.ofSeconds(AnswerTimeWait)))
                                    .then(Mono.defer(() -> addPlayerPoints(messageChannel)))
                                    .then(Mono.defer(() -> closeAnswering(messageChannel)))
                                    .then(Mono.defer(() -> editQuestionMessage(messageChannel, message, index)))
                                    //.then(Mono.defer(() -> createAnswerMessage(messageChannel)))
                                    //.then(Mono.defer(() -> cleanPlayersAnswers(messageChannel)))
                                    .then(Mono.delay(Duration.ofSeconds(newQuestionWait)))
                                    .then(Mono.defer(() -> moveToNextQuestion(match)))
                            );
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


    private Mono<Message> createQuestionMessage(MessageChannel messageChannel, Long questionNumber){
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
                .addField("", questionsAnswers, false)
                .build();

        MessageCreateSpec spec = MessageCreateSpec.builder()
                .addComponent(ActionRow.of(buttons))
                .addEmbed(embed)
                .build();

        return messageChannel.createMessage(spec);
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
                .addField("", questionsAnswers, false)
                .addField("", "Explanation: " + match.getQuestion().getExplanation(), false)
                //.addField("", "Answers:\n" + match.getUsersAnswers(), false)
                .addField("", match.getUsersAnswers(), false)
                .addField("", "Scoreboard:\n" + match.getScoreboard(), false)
                .build();

        return message.edit(MessageEditSpec.builder()
                .addComponent(ActionRow.of(buttons))
                .addEmbed(embed)
                .build());
    }

    public Mono<Message> createStartQuizMessage(MessageChannel messageChannel){
        Match match = quizzes.get(messageChannel);

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("\uD83C\uDFC1 Java Quiz")
                .addField("", "Questions: " + match.getQuestions().size(), false)
                .addField("", "Participants: " + match.getUserNames(), false)
                .addField("", "You have " + participationTimeWait + " seconds to join.", false)
                .build();

        MessageCreateSpec spec = MessageCreateSpec.builder()
                .addComponent(ActionRow.of(Button.success("joinQuiz", "Join"), Button.success("leaveQuiz", "Leave")))
                .addEmbed(embed)
                .build();

        return messageChannel.createMessage(spec);
    }

    private Mono<Message> editStartQuizMessage(Message message, MessageChannel messageChannel){
        Match match = quizzes.get(messageChannel);

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("\uD83C\uDFC1 Java Quiz Lobby")
                .addField("", "Questions: " + match.getQuestions().size(), false)
                .addField("", "Participants: " + match.getUserNames(), false)
                .addField("", "You have " + participationTimeWait + " seconds to join.", false)
                .build();

        return message.edit(MessageEditSpec.builder()
                .addComponent(ActionRow.of(Button.success("joinQuiz", "Join"), Button.success("leaveQuiz", "Leave")))
                .addEmbed(embed)
                .build());
    }

    private Mono<Message> editStartQuizMessage2(Message message, MessageChannel messageChannel){
        Match match = quizzes.get(messageChannel);

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("\uD83C\uDFC1 Java Quiz Lobby")
                .addField("", "Questions: " + match.getQuestions().size(), false)
                .addField("", "Participants: " + match.getUserNames(), false)
                .addField("", "Starting in " + preparationTime + " seconds.", false)
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
                System.out.println("Participants after adding: " + quizzes.get(messageChannel).getUserNames());
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
}


//    private Mono<Message> createAnswerMessage(MessageChannel messageChannel){
//        Match match = quizzes.get(messageChannel);
//
//        EmbedCreateSpec embed = EmbedCreateSpec.builder()
//                .color(Color.of(255, 99, 71))
//                .title("Answer " + match.getQuestionNumber() + ": ")
//                .description("**" + match.getQuestion().getQuestion() + "**")
//                .addField("", "Correct text: " + match.getQuestion().getCorrectAnswer() + " - " + match.getQuestion().getCorrectAnswerString(), true)
//                .addField("", "Explanation: " + match.getQuestion().getExplanation(), false)
//                .addField("", "Answers:\n" + match.getUsersAnswers(), false)
//                .addField("", "Scoreboard:\n" + match.getScoreboard(), false)
//                //.addField("", "Scoreboard:\n" + match.getScoreBoard(), false)
//                .build();
//
//        return messageChannel.createMessage(embed);
//    }