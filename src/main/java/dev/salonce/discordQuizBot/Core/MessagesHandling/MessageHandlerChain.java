package dev.salonce.discordQuizBot.Core.MessagesHandling;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class MessageHandlerChain {
    private final List<MessageHandler> messageHandlers;

    public void handle(DiscordMessage discordMessage){
        for(int i = 0; i < messageHandlers.size(); i++){
            if (messageHandlers.get(i).handleMessage(discordMessage))
                break;
        }

//        for (MessageHandler messageHandler : messageHandlers){
//            if (messageHandler.handleMessage(discordMessage))
//                break;
//        }
    }
}
