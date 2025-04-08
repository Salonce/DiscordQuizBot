package dev.salonce.discordQuizBot.Buttons.Handlers;

import dev.salonce.discordQuizBot.Buttons.*;
import dev.salonce.discordQuizBot.Core.MatchStore;
import dev.salonce.discordQuizBot.Core.Matches.Match;
import dev.salonce.discordQuizBot.Core.Matches.MatchState;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component("ButtonAnswer")
public class AnswerButtonHandler implements ButtonHandler {

    private final MatchStore matchStore;

    @Override
    public boolean handle(ButtonInteractionEvent event, ButtonInteraction buttonInteraction, ButtonInteractionData data) {
        if ("Answer".equals(data.getButtonType())) {
            AnswerInteractionEnum answerEnum = setPlayerAnswer(buttonInteraction);

            String response = switch (answerEnum) {
                case NOT_IN_MATCH -> "You are not in the match.";
                case TOO_LATE -> "Your answer came too late!";
                case VALID -> "Your answer: " + (char) ('A' + data.getAnswerNumber()) + ".";
                default -> "Something went wrong.";
            };

            event.reply(response)
                    .withEphemeral(true)
                    .subscribe();
            return true;
        }
        return false;
    }

    private AnswerInteractionEnum setPlayerAnswer(ButtonInteraction buttonInteraction) {
        Long userId = buttonInteraction.getUserId();
        MessageChannel messageChannel = buttonInteraction.getMessageChannel();
        Match match = matchStore.get(messageChannel);
        String buttonId = buttonInteraction.getButtonId();

        int answerNum = 0;
        int questionNum = 0;

        if (buttonId.matches("Answer-[A-D]-\\d+")) {
            String[] parts = buttonId.split("-");

            // Convert letter to text number (A=0, B=1, C=2, D=3)
            char letterPart = parts[1].charAt(0);
            answerNum = letterPart - 'A';

            // Extract question number
            questionNum = Integer.parseInt(parts[2]);
        }

        if (match == null)
            return AnswerInteractionEnum.TOO_LATE; // could be different

        if (questionNum != match.getCurrentQuestionNum() || match.getMatchState() != MatchState.QUIZ_ANSWERING)
            return AnswerInteractionEnum.TOO_LATE;

        if (match.getPlayers().containsKey(userId)) {
            match.getPlayers().get(userId).getAnswersList().set(questionNum, answerNum);
            return AnswerInteractionEnum.VALID;
        } else return AnswerInteractionEnum.NOT_IN_MATCH;
    }
}
