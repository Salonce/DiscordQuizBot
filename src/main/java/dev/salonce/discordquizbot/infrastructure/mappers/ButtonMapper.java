package dev.salonce.discordquizbot.infrastructure.mappers;

import dev.salonce.discordquizbot.infrastructure.dtos.ButtonInteraction;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;

public class ButtonMapper {
    public static ButtonInteraction toButtonInteractionData(ButtonInteractionEvent buttonInteractionEvent){
        String buttonId = buttonInteractionEvent.getCustomId();
        Long userId = buttonInteractionEvent.getInteraction().getUser().getId().asLong();

        Message message = buttonInteractionEvent.getMessage().orElse(null);
        MessageChannel messageChannel;

        if (message != null)
            messageChannel = message.getChannel().blockOptional().orElse(null);
        else
            messageChannel = null;

        if (buttonId != null && userId != null && messageChannel != null)
            return new ButtonInteraction(userId, buttonId, messageChannel);
        return null;
    }
}
