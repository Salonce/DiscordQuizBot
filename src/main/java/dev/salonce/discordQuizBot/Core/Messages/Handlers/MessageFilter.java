package dev.salonce.discordQuizBot.Core.Messages.Handlers;

import dev.salonce.discordQuizBot.Core.Messages.DiscordMessage;
import dev.salonce.discordQuizBot.Core.Messages.MessageHandler;
import dev.salonce.discordQuizBot.Core.Messages.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static java.lang.Thread.sleep;

@Component("messageFilter")
@RequiredArgsConstructor
public class MessageFilter implements MessageHandler {
    private final MessageSender messageSender;

    @Override
    public boolean handleMessage(DiscordMessage discordMessage){

        //if empty message - ignore and end chain
        if (discordMessage.getContent() == null){
            return true;
        }

        String[] message = discordMessage.getContent().split(" ");

        //if too short message - ignore and end chain
        if (message.length < 2)
            return true;

        //this doesn't do anything now because of previous condition
//        else if (discordMessage.getContent().equalsIgnoreCase("qq")) {
//            messageSender.sendMessage(discordMessage, "Empty request").subscribe();
//            return true;
//        }

        //conditions not met - move on to next chain piece
        return false;
    }
}
