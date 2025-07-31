package dev.salonce.discordquizbot.buttons.handlers;

import dev.salonce.discordquizbot.buttons.ButtonHandler;
import dev.salonce.discordquizbot.buttons.ButtonInteractionData;
import dev.salonce.discordquizbot.core.matches.Match;
import dev.salonce.discordquizbot.core.matches.MatchService;
import dev.salonce.discordquizbot.core.matches.MatchState;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component("ButtonCancelMatch")
public class CancelMatchButtonHandler implements ButtonHandler {

    private final MatchService matchService;

    @Override
    public boolean handle(ButtonInteractionEvent event, ButtonInteractionData buttonInteractionData) {
        if ("cancelQuiz".equals(buttonInteractionData.getButtonId())) {
            event.reply(cancelMatch(buttonInteractionData))
                    .withEphemeral(true)
                    .subscribe();
            return true;
        }
        return false;
    }

    private String cancelMatch (ButtonInteractionData buttonInteractionData) {
        MessageChannel messageChannel = buttonInteractionData.getMessageChannel();
        Match match = matchService.get(messageChannel);
        Long userId = buttonInteractionData.getUserId();
        if (match == null)
            return "This match doesn't exist anymore.";
        if (!match.getOwnerId().equals(userId))
            return "You are not the owner. Only the owner can cancel the match.";
        match.setMatchState(MatchState.CLOSED_BY_OWNER);
        return "With your undeniable power of ownership, you've cancelled the match";
    }
}
