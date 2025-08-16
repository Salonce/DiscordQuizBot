package dev.salonce.discordquizbot.infrastructure.buttonhandlers;

import dev.salonce.discordquizbot.application.ButtonHandler;
import dev.salonce.discordquizbot.infrastructure.dtos.ButtonInteraction;
import dev.salonce.discordquizbot.application.MatchService;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component("ButtonStartNow")
public class StartNowButtonHandler implements ButtonHandler {

    private final MatchService matchService;

    @Override
    public Optional<String> handle(ButtonInteraction buttonInteraction) {

        if (!"startNow".equals(buttonInteraction.buttonId()))
            return Optional.empty();
        return Optional.of(matchService.startNow(buttonInteraction.channelId(), buttonInteraction.userId()));
    }
}



