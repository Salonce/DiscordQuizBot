package dev.salonce.discordQuizBot.Core.MessagesHandling;

public interface MessageHandler {
    boolean handleMessage(DiscordMessage discordMessage);
}
