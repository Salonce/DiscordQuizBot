package dev.salonce.discordQuizBot.Buttons.Handlers;

import dev.salonce.discordQuizBot.Buttons.ButtonHandler;
import dev.salonce.discordQuizBot.Buttons.ButtonInteractionData;
import dev.salonce.discordQuizBot.Core.MatchStore;
import dev.salonce.discordQuizBot.Core.Matches.Match;
import dev.salonce.discordQuizBot.Core.Matches.MatchState;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component("ButtonCancelMatch")
public class CancelMatchButtonHandler implements ButtonHandler {

    private final MatchStore matchStore;

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
        Match match = matchStore.get(messageChannel);
        Long userId = buttonInteractionData.getUserId();
        if (match == null)
            return "This match doesn't exist anymore.";
        if (!match.getOwnerId().equals(userId))
            return "You are not the owner. Only the owner can cancel the match.";
        match.setMatchState(MatchState.CLOSED_BY_OWNER);
        return "With your undeniable power of ownership, you've cancelled the match";
    }
}
