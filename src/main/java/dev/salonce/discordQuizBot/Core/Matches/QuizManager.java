package dev.salonce.discordQuizBot.Core.Matches;

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

    public void addUserToMatch(Message message, MessageChannel messageChannel, User user){
        if (quizzes.containsKey(messageChannel)){
            if (quizzes.get(messageChannel).addPlayer(user)) {
                System.out.println("Participants after adding: " + quizzes.get(messageChannel).getUserNames());
                editStartQuizMessage(message, messageChannel).subscribe();
            }
        }
    }

    public void removeUserFromMatch(Message message, MessageChannel messageChannel, User user){
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

    public boolean setPlayerAnswer(Message message, MessageChannel messageChannel, User user, int intAnswer){
        Match match = quizzes.get(messageChannel);
        if (match.getPlayers().containsKey(user)) {
            match.getPlayers().get(user).setCurrentAnswerNum(intAnswer);
            return true;
        }
        else return false;
    }

    public Mono<Void> cleanPlayersAnswers(MessageChannel messageChannel){
        Match match = quizzes.get(messageChannel);
        match.cleanPlayersAnswers();
        return Mono.empty();
    }

    public void addMatch(MessageChannel messageChannel, Match match) {
        if (quizzes.containsKey(messageChannel)){
            //send message that starting a match is impossible because there is already one
        }
        else{
            quizzes.put(messageChannel, match);
            System.out.println("Initial participants: " + match.getUserNames());
            sendStartQuizMessage(messageChannel)
                    .delayElement(Duration.ofSeconds(enrollmentTime))
                    .then(Mono.defer(() -> startingMatchMessage(messageChannel)))
                    .delayElement(Duration.ofSeconds(preparationTime))
                    .then(Mono.defer(() -> repeatQuestionMessages(messageChannel)))
                    .then(Mono.defer(() -> showMatchResults(messageChannel)))
                    .then(Mono.defer(() -> Mono.just(quizzes.remove(messageChannel))))
                    .subscribe();
        }
    }


    private Mono<Void> repeatQuestionMessages(MessageChannel messageChannel) {
        return sendQuestionsSequentially(messageChannel) // Process all questions sequentially
                //.then(Mono.delay(Duration.ofSeconds(2)))
                .then();
    }

    public Mono<Void> addPlayerPoints(MessageChannel messageChannel){
        quizzes.get(messageChannel).addPlayerPoints();
        return Mono.empty();
    }

    private Mono<Void> sendQuestionsSequentially(MessageChannel messageChannel) {
        Match match = quizzes.get(messageChannel);
        int newQuestionWait = 5; //default is 10, test is 5
        int AnswerTimeWait = 5; //default is 30, test is 5

        return Flux.generate(sink -> {
                    if (match.questionExists()) {
                        System.out.println("question exists or no: " + match.questionExists());
                        sink.next(match.getQuestion());
                        match.nextQuestion();
                    } else {
                        sink.complete();
                    }
                })
                .concatMap(question -> questionMessage(messageChannel)
                        .then(Mono.delay(Duration.ofSeconds(AnswerTimeWait)))
                        .then(Mono.defer(() -> addPlayerPoints(messageChannel)))
                        .then(Mono.defer(() -> questionMessageAnswer(messageChannel)))
                        .then(Mono.defer(() -> cleanPlayersAnswers(messageChannel)))
                        .then(Mono.delay(Duration.ofSeconds(newQuestionWait)))
                )
                .then();
    }

    private Mono<Message> showMatchResults(MessageChannel messageChannel){
        Match match = quizzes.get(messageChannel);

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("Final scoreboard: " )
                .description(match.getScoreboard())
                .addField("", "The winners are: " + match.getWinners(), false)
                .build();

        return messageChannel.createMessage(embed);
    }

    private Mono<Message> questionMessageAnswer(MessageChannel messageChannel){
        Match match = quizzes.get(messageChannel);

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .color(Color.of(255, 99, 71))
                .title("Answer " + match.getQuestionNumber() + ": ")
                .description("**" + match.getQuestion().getQuestion() + "**")
                .addField("", "Correct answer: " + match.getQuestion().getCorrectAnswer() + " - " + match.getQuestion().getCorrectAnswerString(), true)
                .addField("", "Explanation: " + match.getQuestion().getExplanation(), false)
                .addField("", "Answers:\n" + match.getUsersAnswers(), false)
                .addField("", "Scoreboard:\n" + match.getScoreboard(), false)
                //.addField("", "Scoreboard:\n" + match.getScoreBoard(), false)
                .build();

        return messageChannel.createMessage(embed);
    }

    private Mono<Message> questionMessage(MessageChannel messageChannel){
        Match match = quizzes.get(messageChannel);
        String questionsAnswers = match.getQuestion().getStringAnswers();
        int answersSize = match.getQuestion().getAnswers().size();

        // Create buttons dynamically
        List<Button> buttons = new ArrayList<>();
        for (int i = 0; i < answersSize; i++) {
            buttons.add(Button.success("answer" + (char)('A' + i), String.valueOf((char)('A' + i))));
        }

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                //.color(Color.of(255, 99, 71))
                .title("Question " + match.getQuestionNumber() + ": ")
                .description("**" + match.getQuestion().getQuestion() + "**")
                .addField("", questionsAnswers, true)
                .build();

        MessageCreateSpec spec = MessageCreateSpec.builder()
                .addComponent(ActionRow.of(buttons))
                .addEmbed(embed)
                .build();

        return messageChannel.createMessage(spec);
    }

//    private Mono<Message> questionMessage(MessageChannel messageChannel){
//        Match match = quizzes.get(messageChannel);
//        String questionsAnswers = match.getQuestion().getStringAnswers();
//        int questionsSize = match.getQuestions().size();
//
//        EmbedCreateSpec embed = EmbedCreateSpec.builder()
//                .color(Color.of(255, 99, 71))
//                .title("Question " + match.getQuestionNumber() + ": ")
//                .description("**" + match.getQuestion().getQuestion() + "**")
//                .addField("", questionsAnswers, true)
//                .build();
//
//        MessageCreateSpec spec = MessageCreateSpec.builder()
//                .addComponent(ActionRow.of(Button.success("answerA", "A"), Button.success("answerB", "B"), Button.success("answerC", "C"), Button.success("answerD", "D")))
//                .addEmbed(embed)
//                .build();
//
//        return messageChannel.createMessage(spec);
//    }

    private Mono<Message> editStartQuizMessage(Message message, MessageChannel messageChannel){
        Match match = quizzes.get(messageChannel);

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("\uD83C\uDFC1 Java Quiz Lobby")
                .description("You have " + participationTimeWait + " seconds to join.")
                .addField("", "Participants: " + match.getUserNames(), false)
                .build();

        return message.edit(MessageEditSpec.builder()
                .addComponent(ActionRow.of(Button.success("joinQuiz", "Join"), Button.success("leaveQuiz", "Leave")))
                .addEmbed(embed)
                .build());
    }


    public Mono<Message> sendStartQuizMessage(MessageChannel messageChannel){
        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("\uD83C\uDFC1 Java Quiz Lobby")
                .description("You have " + participationTimeWait + " seconds to join.")
                .build();

        MessageCreateSpec spec = MessageCreateSpec.builder()
                .addComponent(ActionRow.of(Button.success("joinQuiz", "Join"), Button.success("leaveQuiz", "Leave")))
                .addEmbed(embed)
                .build();

        return messageChannel.createMessage(spec);
    }
    ////////////
    private Mono<Message> startingMatchMessage(MessageChannel messageChannel){

        Match match = quizzes.get(messageChannel);

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("\uD83D\uDCCB Quiz Setup in Progress")
                //.description("Type: java quiz. Questions: 5. \nParticipants: " + match.getUserNames())
                .addField("", "Questions: 5", true)
                .addField("", "Participants: " + match.getUserNames(), false)
                .addField("", "Starting in " + preparationTime + " seconds", false)
                .build();

        MessageCreateSpec spec = MessageCreateSpec.builder()
                .addEmbed(embed)
                .build();

        return messageChannel.createMessage(spec);
    }

    ////////////
    private Mono<Message> sendParticipantsMessage(MessageChannel messageChannel, String participants){
        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("Participants")
                //.description("Some participants")
                .description(participants)
                .build();

        MessageCreateSpec spec = MessageCreateSpec.builder()
                .addEmbed(embed)
                .build();

        return messageChannel.createMessage(spec);
    }

    //String listing match participants
    private String matchParticipants(List<User> user){
        StringBuilder stringBuilder = new StringBuilder("Match participants: ");
        if (!user.isEmpty()) {
            stringBuilder.append(user.get(0).getMention());
            for (int i = 1; i < user.size(); i++){
                stringBuilder.append(", ");
                stringBuilder.append(user.get(i));
            }
            stringBuilder.append(".");
        }
        else{
            stringBuilder.append("none.");
        }

        return stringBuilder.toString();
    }
    ////////////////

}




//    private void repeatQuestionMessages(MessageChannel messageChannel) {
//        Flux.interval(Duration.ofSeconds(2))  // Emit items every 2 seconds
//                //.doOnNext(tick -> quizzes.get(messageChannel).nextQuestion())
//                .flatMap(tick -> questionMessage(messageChannel)
//                        .subscribeOn(Schedulers.parallel())
//                        //.subscribe(Schedulers.parallel())
//                )
//                .takeWhile(__ -> quizzes.get(messageChannel).nextQuestion() == true)  // Stop when questionMessage returns null
//                .doOnComplete(() -> {
//                    System.out.println("Question messages stopped.");
//
//                })
//                .subscribe();
//    }