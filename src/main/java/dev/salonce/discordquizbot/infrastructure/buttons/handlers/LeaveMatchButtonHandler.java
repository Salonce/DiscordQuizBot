package dev.salonce.discordquizbot.infrastructure.buttons.handlers;

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
        if (!"leaveQuiz".equals(buttonInteractionData.getButtonId()))
            return false;
        String result = matchService.leaveMatch(buttonInteractionData.getMessageChannel(), buttonInteractionData.getUserId());
        event.reply(result)
                .withEphemeral(true)
                .subscribe();
        return true;
    }
}