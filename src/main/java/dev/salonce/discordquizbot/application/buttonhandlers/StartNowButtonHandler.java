package dev.salonce.discordquizbot.application.buttonhandlers;

import dev.salonce.discordquizbot.application.ButtonHandler;
import dev.salonce.discordquizbot.application.ResultStatus;
import dev.salonce.discordquizbot.infrastructure.dtos.ButtonInteraction;
import dev.salonce.discordquizbot.application.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component("ButtonStartNow")
public class StartNowButtonHandler implements ButtonHandler {

    private final MatchService matchService;

    @Override
    public Optional<ResultStatus> handle(ButtonInteraction buttonInteraction) {

        if (!"startNow".equals(buttonInteraction.buttonId()))
            return Optional.empty();
        return Optional.of(matchService.ownerStartsMatch(buttonInteraction.channelId(), buttonInteraction.userId()));
    }
}



