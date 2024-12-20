package dev.salonce.discordQuizBot.Core.Messages.Handlers;

import dev.salonce.discordQuizBot.Core.Messages.DiscordMessage;
import dev.salonce.discordQuizBot.Core.Matches.MatchFactory;
import dev.salonce.discordQuizBot.Core.Matches.QuizManager;
import dev.salonce.discordQuizBot.Core.Messages.MessageHandler;
import dev.salonce.discordQuizBot.QuestionsConfig;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component("startQuiz")
@RequiredArgsConstructor
public class StartQuiz implements MessageHandler {
    private final MatchFactory matchFactory;
    private final QuizManager quizManager;

    private final QuestionsConfig questionsConfig;


    @Override
    public boolean handleMessage(DiscordMessage discordMessage) {
        String[] message = discordMessage.getContent().split(" ");

        if (message[0].equals("qq") && message[1].equals("quiz")){
            if (message.length < 3)
                return true; // too short command. end chain
            String lastMsg = message[2];
            if (questionsConfig.getFiles().containsKey(lastMsg)){
                MessageChannel messageChannel = discordMessage.getChannel();
                quizManager.addMatch(messageChannel, matchFactory.makeMatch(lastMsg));
                return true;
            }
            else{
                //write that the key - type of quiz - doesn't exist
                //return true;
            }
        }
        return false;
    }


//        if (message[0].equals("qq") && message[1].equals("quiz") && message[2].equals("memory")){
//            MessageChannel messageChannel = discordMessage.getChannel();
//            quizManager.addMatch(messageChannel, matchFactory.makeMatch("memory"));
//            return true;
//        }
//
//        if (message[0].equals("qq") && message[1].equals("quiz") && message[2].equals("java")) {
//            MessageChannel messageChannel = discordMessage.getChannel();
//            quizManager.addMatch(messageChannel, matchFactory.makeMatch("java"));
//            return true;
//        }


//    public Mono<Message> sendSpecMessage(MessageChannel messageChannel){
//        EmbedCreateSpec embed = EmbedCreateSpec.builder()
//                .title("Java quiz")
//                .description("Click the button to participate.")
//                .build();
//
//        MessageCreateSpec spec = MessageCreateSpec.builder()
//                .addComponent(ActionRow.of(Button.success("Join", "Join!"), Button.success("Join not", "Don't join.")))
//                .addEmbed(embed)
//                .build();
//
//        return messageChannel.createMessage(spec);
//    }
}
