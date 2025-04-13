package dev.salonce.discordQuizBot.Buttons.Handlers;

import dev.salonce.discordQuizBot.Buttons.ButtonHandler;
import dev.salonce.discordQuizBot.Buttons.ButtonInteractionData;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component("ButtonFallback")
public class FallbackButtonHandler implements ButtonHandler {
    @Override
    public boolean handle(ButtonInteractionEvent event, ButtonInteractionData buttonInteractionData) {
        // This handler always returns true as it's meant to be the last in the chain
        event.reply("Button interaction failed.")
                .withEphemeral(true)
                .subscribe();
        return true;
    }
}
