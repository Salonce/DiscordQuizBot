package dev.salonce.discordQuizBot.MessageHandlers.handlers;

import dev.salonce.discordQuizBot.Core.Match;
import dev.salonce.discordQuizBot.Core.Message;
import dev.salonce.discordQuizBot.Core.Player;
import dev.salonce.discordQuizBot.Core.Question;
import dev.salonce.discordQuizBot.MessageHandlers.MessageHandler;
import dev.salonce.discordQuizBot.Util.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("startQuiz")
@RequiredArgsConstructor
public class StartQuiz implements MessageHandler {
    private final MessageSender messageSender;

    @Override
    public boolean handleMessage(Message message) {
        if (message.getContent().equalsIgnoreCase("qq quiz")) {
            messageSender.sendMessage(message, "A match will start in 60 seconds. Click the button to join.");

            return true;
        }
        return false;
    }
}
