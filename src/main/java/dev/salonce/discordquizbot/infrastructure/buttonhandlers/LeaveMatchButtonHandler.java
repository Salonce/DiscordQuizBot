package dev.salonce.discordquizbot.infrastructure.buttonhandlers;

import dev.salonce.discordquizbot.application.ButtonHandler;
import dev.salonce.discordquizbot.infrastructure.dtos.ButtonInteractionData;
import dev.salonce.discordquizbot.application.MatchService;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component("ButtonLeaveMatch")
public class LeaveMatchButtonHandler implements ButtonHandler {

    private final MatchService matchService;

    @Override
    public boolean handle(ButtonInteractionEvent event, ButtonInteractionData buttonInteractionData) {
        if (!"leaveQuiz".equals(buttonInteractionData.buttonId()))
            return false;
        String result = matchService.leaveMatch(buttonInteractionData.messageChannel(), buttonInteractionData.userId());
        event.reply(result)
                .withEphemeral(true)
                .subscribe();
        return true;
    }
}