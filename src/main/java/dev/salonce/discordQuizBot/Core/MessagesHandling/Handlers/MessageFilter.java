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

        //if empty message - ignore and end chain
//        if (discordMessage.getContent() == null){
//            return true;
//        }
        String content = discordMessage.getContent();

        //if empty message - ignore and end chain
        if (content == null || content.isEmpty()){
            return true;
        }


        String[] message = discordMessage.getContent().split(" ");

        //if too short message - ignore and end chain
        if (message.length < 2)
            return true;

        //conditions not met - move on to next chain piece
        return false;
    }
}




//this doesn't do anything now because of message length condition
//        else if (discordMessage.getContent().equalsIgnoreCase("qq")) {
//            messageSender.sendMessage(discordMessage, "Empty request").subscribe();
//            return true;
//        }