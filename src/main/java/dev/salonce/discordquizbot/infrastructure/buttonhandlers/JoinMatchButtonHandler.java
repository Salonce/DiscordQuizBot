package dev.salonce.discordquizbot.infrastructure.buttonhandlers;

import dev.salonce.discordquizbot.application.ButtonHandler;
import dev.salonce.discordquizbot.infrastructure.dtos.ButtonInteractionData;
import dev.salonce.discordquizbot.application.MatchService;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component("ButtonJoinMatch")
public class JoinMatchButtonHandler implements ButtonHandler {

    private final MatchService matchService;

    @Override
    public boolean handle(ButtonInteractionEvent event, ButtonInteractionData data) {
        if (!"joinQuiz".equals(data.buttonId()))
            return false;
        String result = matchService.addPlayerToMatch(data.messageChannel(), data.userId());
        event.reply(result)
                .withEphemeral(true)
                .subscribe();
        return true;
    }
}
