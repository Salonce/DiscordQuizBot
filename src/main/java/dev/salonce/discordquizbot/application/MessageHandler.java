package dev.salonce.discordquizbot.application;

import dev.salonce.discordquizbot.infrastructure.dtos.DiscordMessage;

public interface MessageHandler {
    boolean handleMessage(DiscordMessage discordMessage);
}
