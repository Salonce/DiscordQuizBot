package dev.salonce.discordquizbot.infrastructure.mappers;

import dev.salonce.discordquizbot.infrastructure.dtos.DiscordMessage;
import discord4j.core.object.entity.Message;
import java.util.Optional;

public class MessageMapper {
    public static Optional<DiscordMessage> toDiscordMessage(Message message) {
        return message.getAuthor()
                .map(author -> new DiscordMessage(
                        author.getId().asLong(),
                        message.getContent(),
                        message.getChannel().block()
                ));
    }
}
