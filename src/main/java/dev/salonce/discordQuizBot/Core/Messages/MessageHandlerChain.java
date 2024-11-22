package dev.salonce.discordQuizBot.Core.Messages;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MessageHandlerChain {
    private final List<MessageHandler> messageHandlers;

    public void handle(DiscordMessage discordMessage){
        for (MessageHandler messageHandler : messageHandlers){
            if (messageHandler.handleMessage(discordMessage))
                break;
        }
    }
}
