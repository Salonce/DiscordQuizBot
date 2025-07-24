package dev.salonce.discordquizbot.core.handlingmessages;

public interface MessageHandler {
    boolean handleMessage(DiscordMessage discordMessage);
}
