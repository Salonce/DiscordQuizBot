package dev.salonce.discordquizbot.infrastructure.dtos;

import discord4j.core.object.entity.channel.MessageChannel;

public record ButtonInteraction(String buttonId,
                                Long userId,
                                MessageChannel messageChannel){
}
