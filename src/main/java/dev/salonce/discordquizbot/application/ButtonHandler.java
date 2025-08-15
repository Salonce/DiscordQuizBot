package dev.salonce.discordquizbot.application;

import dev.salonce.discordquizbot.infrastructure.dtos.ButtonInteraction;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;

public interface ButtonHandler {
    boolean handle(ButtonInteractionEvent event, ButtonInteraction buttonInteraction);
}
