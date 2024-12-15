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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizManager {

    private final Map<MessageChannel, Match> quizzes;

    public QuizManager(){
        quizzes = new HashMap<>();
    }

    public void addUserToMatch(Message message, MessageChannel messageChannel, User user){
        if (quizzes.containsKey(messageChannel)){
            if (quizzes.get(messageChannel).addPlayer(user)) {
                System.out.println("Participants after adding: " + quizzes.get(messageChannel).getUserNames());
                editQuizMessage(message, messageChannel).subscribe();
            }
        }
    }

//    public void addUserToMatch(Message message, MessageChannel messageChannel, User user){
//        if (quizzes.containsKey(messageChannel)){
//            if (quizzes.get(messageChannel).addPlayer(user)) {
//                editQuizMessage(message, messageChannel).subscribe();
//                //messageChannel.createMessage()
//                //send message to the message channel that user is added
//            }
//            else{
//                //change message to the message channel that interacting failed because the match doesn't exist
//            }
//        }
//    }

    public void removeUserFromMatch(Message message, MessageChannel messageChannel, User user){
        if (quizzes.containsKey(messageChannel)){
            if (quizzes.get(messageChannel).removePlayer(user)) {
                editQuizMessage(message, messageChannel).subscribe();
                //messageChannel.createMessage()
                //change message to the message channel that user is removed
            }
            else{
                //change message to the message channel that interaction failed because the match doesn't exist
            }
        }
    }

    @Autowired
    private MessageSender messageSender;

    public void addMatch(MessageChannel messageChannel, Match match) {
        if (quizzes.containsKey(messageChannel)){
            //send message that starting a match is impossible because there is already one
        }
        else{
            quizzes.put(messageChannel, match);
            System.out.println("Initial participants: " + match.getUserNames());
            sendStartQuizMessage(messageChannel)
                    //.doOnNext(a -> System.out.println("inside chain: " + quizzes.get(messageChannel).getUserNames()))
                    .delayElement(Duration.ofSeconds(3))
                    //.doOnNext(a -> System.out.println("inside chain2: " + quizzes.get(messageChannel).getUserNames()))
                    .then(Mono.defer(() -> startingMatchMessage(messageChannel)))
                    //.doOnNext(a -> System.out.println("inside chain3: " + quizzes.get(messageChannel).getUserNames()))
                    .delayElement(Duration.ofSeconds(1))
                    //.doOnNext(a -> System.out.println("inside chain4: " + quizzes.get(messageChannel).getUserNames()))
                    .then(Mono.defer(() -> repeatQuestionMessages(messageChannel)))
                    .then(Mono.defer(() -> showMatchResults(messageChannel)))
                    .subscribe();
//          messageSender.sendChannelMessage(messageChannel, matchParticipants(match.getPlayers())).subscribe();
//          sendSpecMessage(messageChannel, matchParticipants(match.getPlayers()));
        }
    }

    private Mono<Message> showMatchResults(MessageChannel messageChannel){
        Match match = quizzes.get(messageChannel);
        String results = match.getPlayers().entrySet().stream().map(entry -> entry.getKey().getUsername() + ": " + entry.getValue().getPoints()).collect(Collectors.joining("\n"));

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("Match results, users and points: " )
                .description(results)
                .build();

        MessageCreateSpec spec = MessageCreateSpec.builder()
                .addComponent(ActionRow.of(Button.primary("answerA", "A"), Button.success("answerB", "B"), Button.success("answerC", "C"), Button.success("answerD", "D")))
                .addEmbed(embed)
                .build();

        return messageChannel.createMessage(spec);
    }

    private Mono<Void> repeatQuestionMessages(MessageChannel messageChannel) {
        return sendQuestionsSequentially(messageChannel) // Process all questions sequentially
                .then(Mono.delay(Duration.ofSeconds(2)))
                .then(); // Wait 2 seconds after the last question
    }

    private Mono<Void> sendQuestionsSequentially(MessageChannel messageChannel) {
        Match match = quizzes.get(messageChannel);

        return Flux.generate(sink -> {
                    if (match.nextQuestion()) {
                        sink.next(match.getQuestion());
                    } else {
                        sink.complete();
                    }
                })
                .concatMap(question -> questionMessage(messageChannel)
                        .then(Mono.delay(Duration.ofSeconds(2)))
                )
                .then();
    }

    private Mono<Message> questionMessage(MessageChannel messageChannel){
        Match match = quizzes.get(messageChannel);

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("Question number " + match.getQuestionNumber() + ": ")
                .description(match.getQuestion().getQuestion())
                .build();

        MessageCreateSpec spec = MessageCreateSpec.builder()
                .addComponent(ActionRow.of(Button.primary("answerA", "A"), Button.success("answerB", "B"), Button.success("answerC", "C"), Button.success("answerD", "D")))
                .addEmbed(embed)
                .build();

        return messageChannel.createMessage(spec);
    }


    private Mono<Message> editQuizMessage(Message message, MessageChannel messageChannel){
        Match match = quizzes.get(messageChannel);

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("Java quiz")
                .description("Click the button to participate. Participants: " + match.getUserNames())
                .build();

        return message.edit(MessageEditSpec.builder()
                .addComponent(ActionRow.of(Button.success("joinQuiz", "Join"), Button.success("leaveQuiz", "Leave")))
                .addEmbed(embed)
                .build());
    }


    public Mono<Message> sendStartQuizMessage(MessageChannel messageChannel){
        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("Java quiz")
                .description("Click the button to participate.")
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

        System.out.println("dadada: " + match.getUserNames());
        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("Starting match.")
                //.description("Some participants")
                .description("Type: java quiz. Questions: 5. \nParticipants: " + match.getUserNames())
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