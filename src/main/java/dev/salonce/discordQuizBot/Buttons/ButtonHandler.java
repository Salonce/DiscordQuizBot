package dev.salonce.discordQuizBot.Buttons;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;

public interface ButtonHandler {
    boolean handle(ButtonInteractionEvent event, ButtonInteractionData buttonInteractionData);
}
