package dev.salonce.discordquizbot.infrastructure.buttonhandlers;

import dev.salonce.discordquizbot.application.ButtonHandler;
import dev.salonce.discordquizbot.application.ResultStatus;
import dev.salonce.discordquizbot.infrastructure.dtos.ButtonInteraction;
import dev.salonce.discordquizbot.application.MatchService;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component("ButtonJoinMatch")
public class JoinMatchButtonHandler implements ButtonHandler {

    private final MatchService matchService;

    @Override
    public Optional<ResultStatus> handle(ButtonInteraction data) {
        if (!"joinQuiz".equals(data.buttonId()))
            return Optional.empty();
        return Optional.of(matchService.addPlayerToMatch(data.channelId(), data.userId()));
    }
}
