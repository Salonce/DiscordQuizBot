package dev.salonce.discordquizbot.infrastructure.buttonhandlers;

import dev.salonce.discordquizbot.application.ButtonHandler;
import dev.salonce.discordquizbot.infrastructure.dtos.ButtonInteraction;
import dev.salonce.discordquizbot.application.MatchService;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component("ButtonLeaveMatch")
public class LeaveMatchButtonHandler implements ButtonHandler {

    private final MatchService matchService;

    @Override
    public boolean handle(ButtonInteractionEvent event, ButtonInteraction buttonInteraction) {
        if (!"leaveQuiz".equals(buttonInteraction.buttonId()))
            return false;
        String result = matchService.leaveMatch(buttonInteraction.messageChannel().getId().asLong(), buttonInteraction.userId());
        event.reply(result)
                .withEphemeral(true)
                .subscribe();
        return true;
    }
}