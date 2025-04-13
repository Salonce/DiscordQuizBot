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

@RequiredArgsConstructor
@Component("ButtonAnswer")
public class AnswerButtonHandler implements ButtonHandler {

    private final MatchStore matchStore;

    @Override
    public boolean handle(ButtonInteractionEvent event, ButtonInteractionData buttonInteractionData) {
        if (buttonInteractionData.getButtonId().startsWith("Answer")) {
            AnswerData answerData = new AnswerData(buttonInteractionData.getButtonId());
            AnswerInteractionEnum answerEnum = setPlayerAnswer(buttonInteractionData, answerData);

            String response = switch (answerEnum) {
                case NOT_IN_MATCH -> "You are not in the match.";
                case TOO_LATE -> "Your answer came too late!";
                case VALID -> "Your answer: " + (char) ('A' + answerData.getAnswerNumber()) + ".";
                default -> "Something went wrong.";
            };

            event.reply(response)
                    .withEphemeral(true)
                    .subscribe();
            return true;
        }
        return false;
    }

    private AnswerInteractionEnum setPlayerAnswer(ButtonInteractionData buttonInteractionData, AnswerData answerData) {
        Long userId = buttonInteractionData.getUserId();
        Match match = matchStore.get(buttonInteractionData.getMessageChannel());

        if (match == null)
            return AnswerInteractionEnum.TOO_LATE; // could be different

        if (answerData.getQuestionNumber() != match.getCurrentQuestionNum() || match.getMatchState() != MatchState.ANSWERING)
            return AnswerInteractionEnum.TOO_LATE;

        if (match.getPlayers().containsKey(userId)) {
            match.getPlayers().get(userId).getAnswersList().set(answerData.getQuestionNumber(), answerData.getAnswerNumber());
            return AnswerInteractionEnum.VALID;
        }
        return AnswerInteractionEnum.NOT_IN_MATCH;
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
