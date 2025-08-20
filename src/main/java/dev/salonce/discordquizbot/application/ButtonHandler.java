package dev.salonce.discordquizbot.application;

import dev.salonce.discordquizbot.infrastructure.dtos.ButtonInteraction;

import java.util.Optional;

public interface ButtonHandler {
    Optional<ResultStatus> handle(ButtonInteraction buttonInteraction);
}
