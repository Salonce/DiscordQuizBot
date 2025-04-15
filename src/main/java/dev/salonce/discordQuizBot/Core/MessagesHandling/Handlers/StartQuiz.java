package dev.salonce.discordQuizBot.Core.MessagesHandling.Handlers;

import dev.salonce.discordQuizBot.Core.MessagesHandling.DiscordMessage;
import dev.salonce.discordQuizBot.Core.Matches.MatchFactory;
import dev.salonce.discordQuizBot.Core.QuizManager;
import dev.salonce.discordQuizBot.Core.MessagesHandling.MessageHandler;
import dev.salonce.discordQuizBot.Core.Questions.QuestionSetsConfig;
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

        if (!(message[0].equals("qq") && (message[1].equals("quiz") || message[1].equals("start"))))
            return false;

        if (message.length < 3)
            return true; // command good (checked above), but too short, end the chain

        String quizName = message[2];

        if (!questionSetsConfig.getFiles().containsKey(quizName))
            return true; // quiz doesn't exist, maybe send a message that it doesn't

        Long userId = discordMessage.getUser().getId().asLong();
        MessageChannel messageChannel = discordMessage.getChannel();

        quizManager.addMatch(messageChannel, matchFactory.makeMatch(quizName, userId));
        return true;
    }
}
