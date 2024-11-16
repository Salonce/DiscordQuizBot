package dev.salonce.discordQuizBot.MessageHandlers;

import dev.salonce.discordQuizBot.Core.Message;

public interface MessageHandler {
    public boolean handleMessage(Message message);
}
