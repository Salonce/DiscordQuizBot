package dev.salonce.discordquizbot.buttons.handlers;

import dev.salonce.discordquizbot.buttons.ButtonHandler;
import dev.salonce.discordquizbot.buttons.ButtonInteractionData;
import dev.salonce.discordquizbot.core.MatchStore;
import dev.salonce.discordquizbot.core.matches.Match;
import dev.salonce.discordquizbot.core.matches.MatchState;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component("ButtonJoinMatch")
public class JoinMatchButtonHandler implements ButtonHandler {

    private final MatchStore matchStore;

    @Override
    public boolean handle(ButtonInteractionEvent event, ButtonInteractionData buttonInteractionData) {
        if ("joinQuiz".equals(buttonInteractionData.getButtonId())) {
            event.reply(addPlayer(buttonInteractionData))
                    .withEphemeral(true)
                    .subscribe();
            return true;
        }
        return false;
    }

    private String addPlayer(ButtonInteractionData buttonInteractionData){
        MessageChannel messageChannel = buttonInteractionData.getMessageChannel();
        Match match = matchStore.get(messageChannel);
        Long userId = buttonInteractionData.getUserId();

        if (match.getMatchState() != MatchState.ENROLLMENT){
            return "Excuse me, you can join the match only during enrollment phase.";
        }
        if (match.getPlayers().containsKey(userId)) {
            return "Nah... You've already joined the match.";
        }
        else {
            match.addPlayer(userId);
            return "You've joined the match.";
        }
    }
}
