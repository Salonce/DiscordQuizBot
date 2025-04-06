package dev.salonce.discordQuizBot.Core.MessagesHandling.Handlers;

import dev.salonce.discordQuizBot.Core.MessagesHandling.DiscordMessage;
import dev.salonce.discordQuizBot.Core.MessagesSending.HelpMessage;
import dev.salonce.discordQuizBot.Core.QuizManager;
import dev.salonce.discordQuizBot.Core.MessagesHandling.MessageHandler;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component("help")
@RequiredArgsConstructor
public class Help implements MessageHandler {
    private final HelpMessage helpMessage;
    private final QuizManager quizManager;

    @Override
    public boolean handleMessage(DiscordMessage discordMessage) {
        String[] message = discordMessage.getContent().split(" ");

        if (message[0].equals("qq") && message[1].equals("help")) {
                MessageChannel messageChannel = discordMessage.getChannel();
                helpMessage.create(messageChannel).subscribe();
                return true;
        }
        return false;
    }
}