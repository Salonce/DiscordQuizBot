package dev.salonce.discordquizbot.application;

import dev.salonce.discordquizbot.infrastructure.dtos.DiscordMessage;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class MessageHandlerChain {
    private final List<MessageHandler> messageHandlers;

    public void handle(DiscordMessage discordMessage){
        for (MessageHandler messageHandler : messageHandlers) {
            if (messageHandler.handleMessage(discordMessage))
                break;
        }
    }
}
