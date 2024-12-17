package dev.salonce.discordQuizBot;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import lombok.Getter;

@Getter
public class ButtonInteractionData {
    private final String buttonType;
    private final String additionalData;

    public ButtonInteractionData(String buttonId) {
        if (buttonId.equals("joinQuiz")) {
            buttonType = "joinQuiz";
            additionalData = null;
        } else if (buttonId.equals("leaveQuiz")) {
            buttonType = "leaveQuiz";
            additionalData = null;
        } else if (buttonId.matches("[A-D]-\\d+")) {
            String[] parts = buttonId.split("-");
            buttonType = parts[0];
            additionalData = parts[1];
        } else {
            throw new IllegalArgumentException("Invalid button ID: " + buttonId);
        }
    }
}