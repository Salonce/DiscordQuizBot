package dev.salonce.discordquizbot.infrastructure.buttonhandlers;

import dev.salonce.discordquizbot.application.ButtonHandler;
import dev.salonce.discordquizbot.infrastructure.dtos.ButtonInteraction;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component("ButtonFallback")
public class FallbackButtonHandler implements ButtonHandler {
    @Override
    // This handler always returns true as it's meant to be the last in the chain
    public boolean handle(ButtonInteractionEvent event, ButtonInteraction buttonInteraction) {
        event.reply("Button interaction failed.")
                .withEphemeral(true)
                .subscribe();
        return true;
    }
}
