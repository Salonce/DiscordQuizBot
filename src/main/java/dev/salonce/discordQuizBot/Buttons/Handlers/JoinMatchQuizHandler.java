package dev.salonce.discordQuizBot.Buttons.Handlers;

import dev.salonce.discordQuizBot.Buttons.ButtonHandler;
import dev.salonce.discordQuizBot.Buttons.ButtonInteraction;
import dev.salonce.discordQuizBot.Buttons.ButtonInteractionData;
import dev.salonce.discordQuizBot.Core.MatchStore;
import dev.salonce.discordQuizBot.Core.Matches.Match;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component("ButtonJoinMatch")
public class JoinMatchQuizHandler implements ButtonHandler {

    private final MatchStore matchStore;

    @Override
    public boolean handle(ButtonInteractionEvent event, ButtonInteraction buttonInteraction, ButtonInteractionData data) {
        if ("joinQuiz".equals(data.getButtonType())) {
            event.reply(addUserToMatch(buttonInteraction))
                    .withEphemeral(true)
                    .subscribe();
            return true;
        }
        return false;
    }

    private String addUserToMatch(ButtonInteraction buttonInteraction){
        MessageChannel messageChannel = buttonInteraction.getMessageChannel();
        Match match = matchStore.get(messageChannel);
        int questionsNumber = match.getQuestions().size();

        if (matchStore.containsKey(messageChannel)){
            return matchStore.get(messageChannel).addPlayer(buttonInteraction.getUserId(), questionsNumber);
        }
        else{
            return "This match doesn't exist anymore.";
        }
    }
}
