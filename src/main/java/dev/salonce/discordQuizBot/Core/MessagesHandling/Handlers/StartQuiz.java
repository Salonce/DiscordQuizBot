package dev.salonce.discordQuizBot.Core.MessagesHandling.Handlers;

import dev.salonce.discordQuizBot.Core.MessagesHandling.DiscordMessage;
import dev.salonce.discordQuizBot.Core.Matches.MatchFactory;
import dev.salonce.discordQuizBot.Core.Questions.TopicService;
import dev.salonce.discordQuizBot.Core.QuizManager;
import dev.salonce.discordQuizBot.Core.MessagesHandling.MessageHandler;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component("startQuiz")
@RequiredArgsConstructor
public class StartQuiz implements MessageHandler {
    private final MatchFactory matchFactory;
    private final QuizManager quizManager;

    private final TopicService topicService;


    @Override
    public boolean handleMessage(DiscordMessage discordMessage) {
        String[] message = discordMessage.getContent().split(" ");

        if (!(message[0].equals("qq") && (message[1].equals("quiz") || message[1].equals("start"))))
            return false;

        if (message.length < 4)
            return true; // command good (checked above), but too short, end the chain

        int difficulty;
        try{
            difficulty = Integer.parseInt(message[3]);
        } catch (NumberFormatException e) {
            return true; // end the chain - wrong integer, perhaps send a message that it is wrong
        }

        String topic = message[2];
        if (!topicService.doesQuestionSetExist(topic, difficulty))
            return true;

        Long userId = discordMessage.getUser().getId().asLong();
        MessageChannel messageChannel = discordMessage.getChannel();

        quizManager.addMatch(messageChannel, matchFactory.makeMatch(topic, difficulty, userId));
        return true;
    }
}
