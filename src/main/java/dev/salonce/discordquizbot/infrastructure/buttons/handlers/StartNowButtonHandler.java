package dev.salonce.discordquizbot.infrastructure.buttons.handlers;

import dev.salonce.discordquizbot.infrastructure.buttons.ButtonHandler;
import dev.salonce.discordquizbot.infrastructure.buttons.ButtonInteractionData;
import dev.salonce.discordquizbot.application.MatchService;
import dev.salonce.discordquizbot.domain.MatchState;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@RequiredArgsConstructor
@Component("ButtonStartNow")
public class StartNowButtonHandler implements ButtonHandler {

    private final MatchService matchService;

    @Override
    public boolean handle(ButtonInteractionEvent event, ButtonInteractionData buttonInteractionData) {
        if ("startNow".equals(buttonInteractionData.getButtonId())) {
            event.reply(startNow(buttonInteractionData))
                    .withEphemeral(true)
                    .subscribe();
            return true;
        }
        return false;
    }

    private String startNow(ButtonInteractionData buttonInteractionData) {
        MessageChannel messageChannel = buttonInteractionData.getMessageChannel();
        Long userId = buttonInteractionData.getUserId();

        if (!matchService.containsKey(messageChannel))
            return "This match doesn't exist anymore.";
        if (!Objects.equals(userId, matchService.get(messageChannel).getOwnerId()))
            return "You aren't the owner";
        if (matchService.get(messageChannel).getMatchState() != MatchState.ENROLLMENT)
            return "Already started";

        matchService.get(messageChannel).setMatchState(MatchState.COUNTDOWN);
        return "Starting immediately";
    }
}



