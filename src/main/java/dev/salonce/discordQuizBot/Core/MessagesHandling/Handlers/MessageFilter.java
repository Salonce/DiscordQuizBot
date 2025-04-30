package dev.salonce.discordQuizBot.Core.MessagesHandling.Handlers;

import dev.salonce.discordQuizBot.Core.MessagesHandling.DiscordMessage;
import dev.salonce.discordQuizBot.Core.MessagesHandling.MessageHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static java.lang.Thread.sleep;

@Component("messageFilter")
@RequiredArgsConstructor
public class MessageFilter implements MessageHandler {

    @Override
    public boolean handleMessage(DiscordMessage discordMessage){

        String content = discordMessage.getContent();

        if (content == null || content.isEmpty())
            return true;

        if (!content.startsWith("qq"))
            return true;

        if (content.length() > 50)
            return true;

        String[] message = discordMessage.getContent().split(" ");

        if (message.length < 2)
            return true;

        if (message.length > 6)
            return true;

        return false;
    }
}




//this doesn't do anything now because of message length condition
//        else if (discordMessage.getContent().equalsIgnoreCase("qq")) {
//            messageSender.sendMessage(discordMessage, "Empty request").subscribe();
//            return true;
//        }