package dev.salonce.discordQuizBot.Core.MessagesHandling.Handlers;

import dev.salonce.discordQuizBot.Core.MessagesHandling.DiscordMessage;
import dev.salonce.discordQuizBot.Core.Matches.MatchFactory;
import dev.salonce.discordQuizBot.Configs.AvailableTopicsConfig;
import dev.salonce.discordQuizBot.Core.Questions.RawQuestionService;
import dev.salonce.discordQuizBot.Core.QuizManager;
import dev.salonce.discordQuizBot.Core.MessagesHandling.MessageHandler;
import dev.salonce.discordQuizBot.Core.Questions.RawQuestionRepository;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component("startQuiz")
@RequiredArgsConstructor
public class StartQuiz implements MessageHandler {
    private final MatchFactory matchFactory;
    private final QuizManager quizManager;

    private final RawQuestionService rawQuestionService;
    private final AvailableTopicsConfig availableTopicsConfig;


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

        String tag = message[2];
        System.out.println("quiz exists not");
        if (!rawQuestionService.doesQuestionSetExist(tag, difficulty))
            return true; // quiz doesn't exist, maybe send a message that it doesn't
        System.out.println("quiz exists");
//        if (!availableTopicsConfig.getAvailableTopics().containsKey(tag))
//            return true; // quiz doesn't exist, maybe send a message that it doesn't

        Long userId = discordMessage.getUser().getId().asLong();
        MessageChannel messageChannel = discordMessage.getChannel();

        quizManager.addMatch(messageChannel, matchFactory.makeMatch(tag, difficulty, userId));
        System.out.println("match made");
        return true;
    }
}
