package dev.salonce.discordquizbot.infrastructure.messages.in;

import dev.salonce.discordquizbot.infrastructure.dtos.DiscordMessage;
import dev.salonce.discordquizbot.infrastructure.messages.out.HelpMessage;
import dev.salonce.discordquizbot.application.QuizManager;
import dev.salonce.discordquizbot.application.MessageHandler;
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