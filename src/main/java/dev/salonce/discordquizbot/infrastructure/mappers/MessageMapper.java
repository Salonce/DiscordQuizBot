package dev.salonce.discordquizbot.infrastructure.mappers;

import dev.salonce.discordquizbot.infrastructure.dtos.DiscordMessage;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;

import java.util.NoSuchElementException;
import java.util.Optional;

public class MessageMapper {
    public static Optional<DiscordMessage> toDiscordMessage(Message message) {
        return message.getAuthor()
                .map(author -> new DiscordMessage(
                        message.getChannel().block(),
                        author,
                        "<@" + author.getId().asString() + ">",
                        author.getUsername(),
                        author.getId().asLong(),
                        author.getAvatarUrl(),
                        message.getContent()
                ));
    }
}
