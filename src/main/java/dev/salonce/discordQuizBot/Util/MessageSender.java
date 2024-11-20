package dev.salonce.discordQuizBot.Util;

import dev.salonce.discordQuizBot.Core.DiscordMessage;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class MessageSender {

    public Mono<Message> sendMessage(DiscordMessage incomingDiscordMessage, String text) {
        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .author(incomingDiscordMessage.getUserName(), null, incomingDiscordMessage.getUserAvatarUrl())
                .title(text)
                .build();
        return sendMessage(incomingDiscordMessage, embed);
    }

    public Mono<Message> sendMessage(DiscordMessage discordMessage, EmbedCreateSpec embedMessage) {
        return discordMessage.getChannel().createMessage(embedMessage);
    }
}
