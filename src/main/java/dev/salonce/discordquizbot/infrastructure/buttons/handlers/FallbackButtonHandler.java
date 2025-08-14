package dev.salonce.discordquizbot.infrastructure.buttons.handlers;

import dev.salonce.discordquizbot.infrastructure.buttons.ButtonHandler;
import dev.salonce.discordquizbot.infrastructure.dtos.ButtonInteractionData;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component("ButtonFallback")
public class FallbackButtonHandler implements ButtonHandler {
    @Override
    // This handler always returns true as it's meant to be the last in the chain
    public boolean handle(ButtonInteractionEvent event, ButtonInteractionData buttonInteractionData) {
        event.reply("Button interaction failed.")
                .withEphemeral(true)
                .subscribe();
        return true;
    }
}
