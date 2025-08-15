package dev.salonce.discordquizbot.infrastructure.dtos;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;

public record ButtonInteractionData (String buttonId,
                                     User user,
                                     Long userId,
                                     Message message,
                                     MessageChannel messageChannel){
}
