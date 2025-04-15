package dev.salonce.discordQuizBot.Buttons.Handlers;

import dev.salonce.discordQuizBot.Buttons.*;
import dev.salonce.discordQuizBot.Core.MatchStore;
import dev.salonce.discordQuizBot.Core.Matches.Match;
import dev.salonce.discordQuizBot.Core.Matches.MatchState;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component("ButtonAnswer")
public class AnswerButtonHandler implements ButtonHandler {

    private final MatchStore matchStore;

    @Override
    public boolean handle(ButtonInteractionEvent event, ButtonInteractionData buttonInteractionData) {
        if (buttonInteractionData.getButtonId().startsWith("Answer")) {
            AnswerData answerData = new AnswerData(buttonInteractionData.getButtonId());
            String response = getPlayerAnswer(buttonInteractionData, answerData);

            event.reply(response)
                    .withEphemeral(true)
                    .subscribe();
            return true;
        }
        return false;
    }

    private String getPlayerAnswer(ButtonInteractionData buttonInteractionData, AnswerData answerData) {
        Long userId = buttonInteractionData.getUserId();
        Match match = matchStore.get(buttonInteractionData.getMessageChannel());
        int questionNumber = answerData.getQuestionNumber();
        int answerNumber = answerData.getAnswerNumber();

        if (match == null || questionNumber != match.getCurrentQuestionNum() || match.getMatchState() != MatchState.ANSWERING)
            return "Your answer came too late!";

        if (!match.getPlayers().containsKey(userId))
            return "You are not in the match.";

        List<Integer> answers = match.getPlayers().get(userId).getAnswersList();
        answers.set(questionNumber, answerNumber);
        return "Your answer: " + (char) ('A' + answerNumber) + ".";

    }
}

@Getter
class AnswerData {
    private final int questionNumber;
    private final int answerNumber;

    public AnswerData(String buttonId) {
        if (!buttonId.matches("Answer-[A-D]-\\d+"))
            throw new IllegalArgumentException("Invalid button ID format: " + buttonId);
        String[] parts = buttonId.split("-");
        answerNumber = parts[1].charAt(0) - 'A';  // Convert letter to text number (A=0, B=1, C=2, D=3)
        questionNumber = Integer.parseInt(parts[2]);
    }
}
