package dev.salonce.discordQuizBot.Util;

import dev.salonce.discordQuizBot.Core.Message;
import discord4j.core.spec.EmbedCreateSpec;
import org.springframework.stereotype.Component;

@Component
public class MessageSender {

    public void sendMessage(Message incomingMessage, String text) {
        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .author(incomingMessage.getUserName(), null, incomingMessage.getUserAvatarUrl())
                .title(text)
                .build();
        sendMessage(incomingMessage, embed);
    }

    private void sendMessage(Message message, EmbedCreateSpec embedMessage) {
        message.getChannel().createMessage(embedMessage).block();
    }
}
