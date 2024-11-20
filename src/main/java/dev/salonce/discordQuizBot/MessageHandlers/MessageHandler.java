package dev.salonce.discordQuizBot.MessageHandlers;

import dev.salonce.discordQuizBot.Core.DiscordMessage;

public interface MessageHandler {
    boolean handleMessage(DiscordMessage discordMessage);
}
