package dev.salonce.discordquizbot.buttons;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;

public interface ButtonHandler {
    boolean handle(ButtonInteractionEvent event, ButtonInteractionData buttonInteractionData);
}
