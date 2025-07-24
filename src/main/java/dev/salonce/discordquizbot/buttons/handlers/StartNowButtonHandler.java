package dev.salonce.discordquizbot.buttons.handlers;

import dev.salonce.discordquizbot.buttons.ButtonHandler;
import dev.salonce.discordquizbot.buttons.ButtonInteractionData;
import dev.salonce.discordquizbot.core.MatchStore;
import dev.salonce.discordquizbot.core.matches.MatchState;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@RequiredArgsConstructor
@Component("ButtonStartNow")
public class StartNowButtonHandler implements ButtonHandler {

    private final MatchStore matchStore;

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

        if (!matchStore.containsKey(messageChannel))
            return "This match doesn't exist anymore.";
        if (!Objects.equals(userId, matchStore.get(messageChannel).getOwnerId()))
            return "You aren't the owner";
        if (matchStore.get(messageChannel).getMatchState() != MatchState.ENROLLMENT)
            return "Already started";

        matchStore.get(messageChannel).setMatchState(MatchState.COUNTDOWN);
        return "Starting immediately";
    }
}



