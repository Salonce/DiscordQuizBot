package dev.salonce.discordquizbot.infrastructure.messages.in;

import dev.salonce.discordquizbot.infrastructure.dtos.DiscordMessage;
import dev.salonce.discordquizbot.application.MessageHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static java.lang.Thread.sleep;

@Component("messageFilter")
@RequiredArgsConstructor
public class MessageFilter implements MessageHandler {

    @Override
    public boolean handleMessage(DiscordMessage discordMessage){

        String content = discordMessage.content();

        if (content == null || content.isEmpty()) return true;
        if (!content.startsWith("qq")) return true;
        if (content.length() > 50)  return true;

        String[] message = discordMessage.content().split(" ");
        return message.length < 2 || message.length > 6;
    }
}