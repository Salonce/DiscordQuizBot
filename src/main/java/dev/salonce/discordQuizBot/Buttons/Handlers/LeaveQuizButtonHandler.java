package dev.salonce.discordQuizBot.Buttons.Handlers;

import dev.salonce.discordQuizBot.Buttons.ButtonHandler;
import dev.salonce.discordQuizBot.Buttons.ButtonInteraction;
import dev.salonce.discordQuizBot.Buttons.ButtonInteractionData;
import dev.salonce.discordQuizBot.Core.MatchStore;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component("ButtonLeaveMatch")
public class LeaveQuizButtonHandler implements ButtonHandler {

    private final MatchStore matchStore;

    @Override
    public boolean handle(ButtonInteractionEvent event, ButtonInteraction buttonInteraction, ButtonInteractionData data) {
        if ("leaveQuiz".equals(data.getButtonType())) {
            event.reply(removeUserFromMatch(buttonInteraction))
                    .withEphemeral(true)
                    .subscribe();
            return true;
        }
        return false;
    }

    private String removeUserFromMatch(ButtonInteraction buttonInteraction){
        MessageChannel messageChannel = buttonInteraction.getMessageChannel();

        if (matchStore.containsKey(messageChannel))
            return matchStore.get(messageChannel).removePlayer(buttonInteraction.getUserId());
        else{
            return "This match doesn't exist anymore.";
        }
    }
}