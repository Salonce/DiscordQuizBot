package dev.salonce.discordquizbot.infrastructure.buttonhandlers;

import dev.salonce.discordquizbot.application.ButtonHandler;
import dev.salonce.discordquizbot.application.ResultStatus;
import dev.salonce.discordquizbot.infrastructure.dtos.ButtonInteraction;
import dev.salonce.discordquizbot.application.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component("ButtonCancelMatch")
public class CancelMatchButtonHandler implements ButtonHandler {

    private final MatchService matchService;

    @Override
    public Optional<ResultStatus> handle(ButtonInteraction data) {
        if (!"cancelQuiz".equals(data.buttonId()))
            return Optional.empty();
        return Optional.of(matchService.ownerCancelsMatch(data.channelId(), data.userId()));
    }
}
