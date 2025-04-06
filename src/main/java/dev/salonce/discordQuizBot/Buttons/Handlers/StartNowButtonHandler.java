package dev.salonce.discordQuizBot.Buttons.Handlers;

import dev.salonce.discordQuizBot.Buttons.ButtonHandler;
import dev.salonce.discordQuizBot.Buttons.ButtonInteraction;
import dev.salonce.discordQuizBot.Buttons.ButtonInteractionData;
import dev.salonce.discordQuizBot.Buttons.ButtonInteractions;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component("ButtonStartNow")
public class StartNowButtonHandler implements ButtonHandler {
    private final ButtonInteractions buttonInteractions;

    @Override
    public boolean handle(ButtonInteractionEvent event, ButtonInteraction buttonInteraction, ButtonInteractionData data) {
        if ("startNow".equals(data.getButtonType())) {
            event.reply(buttonInteractions.startNow(buttonInteraction))
                    .withEphemeral(true)
                    .subscribe();
            return true;
        }
        return false;
    }
}
