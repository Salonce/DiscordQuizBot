package dev.salonce.discordquizbot.infrastructure.buttons;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.Getter;

@Getter
public class ButtonInteractionData {

    private final String buttonId;
    private final User user;
    private final Long userId;
    private final Message message;
    private final MessageChannel messageChannel;

    public ButtonInteractionData(ButtonInteractionEvent buttonInteractionEvent){
        this.buttonId = buttonInteractionEvent.getCustomId();
        this.user = buttonInteractionEvent.getInteraction().getUser();
        this.userId = user.getId().asLong();
        this.message = buttonInteractionEvent.getMessage().orElse(null);
        if (message != null)
            messageChannel = message.getChannel().blockOptional().orElse(null);
        else
            messageChannel = null;
    }

    public boolean buttonEventValid(){
        return (buttonId != null && user != null && message != null && messageChannel != null);
    }
}
