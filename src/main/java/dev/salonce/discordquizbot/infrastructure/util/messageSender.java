package dev.salonce.discordquizbot.infrastructure.util;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class messageSender {
    public Mono<Message> send(MessageChannel channel, EmbedCreateSpec embed) {
        return channel.createMessage(embed);
    }

    public Mono<Message> send(MessageChannel channel, MessageCreateSpec spec) {
        return channel.createMessage(spec);
    }

    public Mono<Message> edit(Message message, MessageEditSpec spec) {
        return message.edit(spec);
    }
}
