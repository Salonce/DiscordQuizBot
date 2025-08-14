package dev.salonce.discordquizbot.infrastructure.buttons.handlers;

import dev.salonce.discordquizbot.infrastructure.buttons.ButtonHandler;
import dev.salonce.discordquizbot.infrastructure.buttons.ButtonInteractionData;
import dev.salonce.discordquizbot.application.MatchService;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@RequiredArgsConstructor
@Component("ButtonStartNow")
public class StartNowButtonHandler implements ButtonHandler {

    private final MatchService matchService;

    @Override
    public boolean handle(ButtonInteractionEvent event, ButtonInteractionData buttonInteractionData) {
        if (!"startNow".equals(buttonInteractionData.getButtonId()))
            return false;
        String result = matchService.startNow(buttonInteractionData.getMessageChannel(), buttonInteractionData.getUserId());
        event.reply(result)
                .withEphemeral(true)
                .subscribe();
        return true;
    }
}



