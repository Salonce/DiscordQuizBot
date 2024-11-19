package dev.salonce.discordQuizBot.MessageHandlers;

import dev.salonce.discordQuizBot.Core.Message;

public interface MessageHandler {
    boolean handleMessage(Message message);
}
