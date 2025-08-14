package dev.salonce.discordquizbot.application;

import dev.salonce.discordquizbot.infrastructure.dtos.ButtonInteractionData;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;

public interface ButtonHandler {
    boolean handle(ButtonInteractionEvent event, ButtonInteractionData buttonInteractionData);
}
