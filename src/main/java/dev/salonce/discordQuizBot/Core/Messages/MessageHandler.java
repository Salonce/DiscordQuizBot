package dev.salonce.discordQuizBot.Core.Messages;

public interface MessageHandler {
    boolean handleMessage(DiscordMessage discordMessage);
}
