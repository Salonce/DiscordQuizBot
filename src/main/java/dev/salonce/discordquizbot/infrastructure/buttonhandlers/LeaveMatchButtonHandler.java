package dev.salonce.discordquizbot.infrastructure.buttonhandlers;

import dev.salonce.discordquizbot.application.ButtonHandler;
import dev.salonce.discordquizbot.infrastructure.dtos.ButtonInteraction;
import dev.salonce.discordquizbot.application.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;


@RequiredArgsConstructor
@Component("ButtonLeaveMatch")
public class LeaveMatchButtonHandler implements ButtonHandler {

    private final MatchService matchService;

    @Override
    public Optional<String> handle(ButtonInteraction buttonInteraction) {
        if (!"leaveQuiz".equals(buttonInteraction.buttonId()))
            return Optional.empty();
        return Optional.of(matchService.removeUserFromMatch(buttonInteraction.channelId(), buttonInteraction.userId()));
    }
}