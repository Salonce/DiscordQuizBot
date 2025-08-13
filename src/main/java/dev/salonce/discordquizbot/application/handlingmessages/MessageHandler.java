package dev.salonce.discordquizbot.application.handlingmessages;

import dev.salonce.discordquizbot.infrastructure.dtos.DiscordMessage;

public interface MessageHandler {
    boolean handleMessage(DiscordMessage discordMessage);
}
