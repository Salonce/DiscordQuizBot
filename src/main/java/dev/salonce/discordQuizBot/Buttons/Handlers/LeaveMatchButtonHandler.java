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
@Component("ButtonLeaveMatch")
public class LeaveMatchButtonHandler implements ButtonHandler {

    private final MatchStore matchStore;

    @Override
    public boolean handle(ButtonInteractionEvent event, ButtonInteractionData buttonInteractionData) {
        if ("leaveQuiz".equals(buttonInteractionData.getButtonId())) {
            event.reply(leaveMatch(buttonInteractionData))
                    .withEphemeral(true)
                    .subscribe();
            return true;
        }
        return false;
    }

    private String leaveMatch(ButtonInteractionData buttonInteractionData){
        MessageChannel messageChannel = buttonInteractionData.getMessageChannel();
        Match match = matchStore.get(messageChannel);
        Long userId = buttonInteractionData.getUserId();
        if (match == null) {
            return "This match doesn't exist.";
        }
        if (!match.getPlayers().containsKey(userId)){
            return "You are not even in the match.";
        }
        if (match.getMatchState() != MatchState.ENROLLMENT) {
            return "Excuse me, you can leave the match only during enrollment phase.";
        }
        else {
            match.getPlayers().remove(userId);
            return "You've left the match.";
        }
    }
}