package dev.salonce.discordQuizBot.Buttons.Handlers;

import dev.salonce.discordQuizBot.Buttons.*;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component("ButtonAnswer")
public class AnswerButtonHandler implements ButtonHandler {
    private final ButtonInteractions buttonInteractions;

    @Override
    public boolean handle(ButtonInteractionEvent event, ButtonInteraction buttonInteraction, ButtonInteractionData data) {
        if ("Answer".equals(data.getButtonType())) {
            AnswerInteractionEnum answerEnum = buttonInteractions.setPlayerAnswer(buttonInteraction);

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
}
