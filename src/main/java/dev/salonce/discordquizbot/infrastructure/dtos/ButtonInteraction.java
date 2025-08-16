package dev.salonce.discordquizbot.infrastructure.dtos;

public record ButtonInteraction(Long userId,
                                Long channelId,
                                String buttonId){
}
