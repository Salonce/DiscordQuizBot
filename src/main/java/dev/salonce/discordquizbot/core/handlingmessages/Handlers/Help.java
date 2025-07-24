package dev.salonce.discordquizbot.core.handlingmessages.Handlers;

import dev.salonce.discordquizbot.core.handlingmessages.DiscordMessage;
import dev.salonce.discordquizbot.core.sendingmessages.HelpMessage;
import dev.salonce.discordquizbot.core.QuizManager;
import dev.salonce.discordquizbot.core.handlingmessages.MessageHandler;
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