package dev.salonce.discordQuizBot.Buttons;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.Getter;

@Getter
public class ButtonInteraction {

    private final String buttonId;
    private final User user;
    private final Message message;
    private final MessageChannel messageChannel;

    public ButtonInteraction(ButtonInteractionEvent buttonInteractionEvent){
        buttonId = buttonInteractionEvent.getCustomId();
        user = buttonInteractionEvent.getInteraction().getUser();
        message = buttonInteractionEvent.getMessage().orElse(null);
        if (message != null)
            messageChannel = message.getChannel().blockOptional().orElse(null);
        else
            messageChannel = null;
    }

    public boolean buttonEventValid(){
        return (buttonId != null && user != null && message != null && messageChannel != null);
    }
}
