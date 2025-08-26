package dev.salonce.discordquizbot.application;

import dev.salonce.discordquizbot.infrastructure.dtos.ButtonInteraction;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ButtonHandlerChain {
    private final List<ButtonHandler> buttonHandlers;

    public Optional<ResultStatus> handle(ButtonInteraction buttonInteraction) {
        for (ButtonHandler handler : buttonHandlers) {
            Optional<ResultStatus> optionalResult = handler.handle(buttonInteraction);
            if (handler.handle(buttonInteraction).isPresent()) {
                return optionalResult;
            }
        }
        return Optional.empty();
    }
}
