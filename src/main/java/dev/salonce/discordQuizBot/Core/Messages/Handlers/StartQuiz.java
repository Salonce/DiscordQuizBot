package dev.salonce.discordQuizBot.Core.Messages.Handlers;

import dev.salonce.discordQuizBot.Core.Messages.DiscordMessage;
import dev.salonce.discordQuizBot.Core.Matches.MatchFactory;
import dev.salonce.discordQuizBot.QuizManager;
import dev.salonce.discordQuizBot.Core.Messages.MessageHandler;
import dev.salonce.discordQuizBot.Configs.QuestionSetsConfig;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component("startQuiz")
@RequiredArgsConstructor
public class StartQuiz implements MessageHandler {
    private final MatchFactory matchFactory;
    private final QuizManager quizManager;

    private final QuestionSetsConfig questionSetsConfig;


    @Override
    public boolean handleMessage(DiscordMessage discordMessage) {
        String[] message = discordMessage.getContent().split(" ");

        if (message[0].equals("qq") && message[1].equals("quiz")){
            if (message.length < 3)
                return true; // too short command. end chain
            String lastMsg = message[2];
            if (questionSetsConfig.getFiles().containsKey(lastMsg)){
                User user = discordMessage.getUser();
                //Long ownerId = discordMessage.getUsernameIdLong();
                MessageChannel messageChannel = discordMessage.getChannel();
                quizManager.addMatch(messageChannel, matchFactory.makeMatch(lastMsg, user));
                return true;
            }
            else{
                //write that the key - type of quiz - doesn't exist
                //return true;
            }
        }
        return false;
    }

}
