package dev.salonce.discordQuizBot.MessageHandlers.handlers;

import dev.salonce.discordQuizBot.Core.Message;
import dev.salonce.discordQuizBot.MessageHandlers.MessageHandler;
import dev.salonce.discordQuizBot.Util.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("messageFilter")
@RequiredArgsConstructor
public class MessageFilter implements MessageHandler {
    private final MessageSender messageSender;

    @Override
    public boolean handleMessage(Message message) {
        if (message.getContent().equalsIgnoreCase("qq")) {
            messageSender.sendMessage(message, "Empty request");
            return true;
        }
        return false;
    }
}
