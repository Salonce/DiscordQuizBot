package dev.salonce.discordquizbot.application;

import dev.salonce.discordquizbot.infrastructure.dtos.ButtonInteraction;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ButtonHandlerChain {
    private final List<ButtonHandler> buttonHandlers;

    public void handle(ButtonInteractionEvent event, ButtonInteraction buttonInteraction) {
        for (ButtonHandler handler : buttonHandlers) {
            if (handler.handle(event, buttonInteraction)) {
                break;
            }
        }
    }
}
