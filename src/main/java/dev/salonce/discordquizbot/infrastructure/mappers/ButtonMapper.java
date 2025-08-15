package dev.salonce.discordquizbot.infrastructure.mappers;

import dev.salonce.discordquizbot.infrastructure.dtos.ButtonInteractionData;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;

public class ButtonMapper {
    public static ButtonInteractionData toButtonInteractionData(ButtonInteractionEvent buttonInteractionEvent){
        String buttonId = buttonInteractionEvent.getCustomId();
        User user = buttonInteractionEvent.getInteraction().getUser();
        Long userId = user.getId().asLong();
        Message message = buttonInteractionEvent.getMessage().orElse(null);
        MessageChannel messageChannel;

        if (message != null)
            messageChannel = message.getChannel().blockOptional().orElse(null);
        else
            messageChannel = null;

        if (buttonId != null && user != null && userId != null && message != null && messageChannel != null)
            return new ButtonInteractionData(buttonId, user, userId, message, messageChannel);
        return null;
    }
}
