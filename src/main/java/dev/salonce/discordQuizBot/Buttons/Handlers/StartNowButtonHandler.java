package dev.salonce.discordQuizBot.Buttons.Handlers;

import dev.salonce.discordQuizBot.Buttons.ButtonHandler;
import dev.salonce.discordQuizBot.Buttons.ButtonInteraction;
import dev.salonce.discordQuizBot.Buttons.ButtonInteractionData;
import dev.salonce.discordQuizBot.Core.MatchStore;
import dev.salonce.discordQuizBot.Core.Matches.MatchState;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component("ButtonStartNow")
public class StartNowButtonHandler implements ButtonHandler {

    private final MatchStore matchStore;

    @Override
    public boolean handle(ButtonInteractionEvent event, ButtonInteraction buttonInteraction, ButtonInteractionData data) {
        if ("startNow".equals(data.getButtonType())) {
            event.reply(startNow(buttonInteraction))
                    .withEphemeral(true)
                    .subscribe();
            return true;
        }
        return false;
    }

    private String startNow(ButtonInteraction buttonInteraction){
        MessageChannel messageChannel = buttonInteraction.getMessageChannel();



        if (matchStore.containsKey(messageChannel)){
            //i think everyone is allowed now to start because of lack of ownership check, should be also in if (userId == ownerId...)
            if (matchStore.get(messageChannel).getMatchState() != MatchState.COUNTDOWN) {
                matchStore.get(messageChannel).setMatchState(MatchState.COUNTDOWN);
                return "Starting immediately";
            }
            else
                return "Already started";
        }
        else{
            return "This match doesn't exist anymore.";
        }
    }
}
