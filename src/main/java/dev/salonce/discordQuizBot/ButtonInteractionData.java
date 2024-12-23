package dev.salonce.discordQuizBot;

import lombok.Getter;

@Getter
public class ButtonInteractionData {
    private final String buttonType;
    private final int questionNumber;
    private final int answerNumber;

    public ButtonInteractionData(String buttonId) {
        if (buttonId.equals("joinQuiz")) {
            buttonType = "joinQuiz";
            questionNumber = -1;
            answerNumber = -1;
        } else if (buttonId.equals("leaveQuiz")) {
            buttonType = "leaveQuiz";
            questionNumber = -1;
            answerNumber = -1;
        } else if (buttonId.equals("cancelQuiz")) {
            buttonType = "cancelQuiz";
            questionNumber = -1;
            answerNumber = -1;
        } else if (buttonId.matches("Answer-[A-D]-\\d+")) {
            String[] parts = buttonId.split("-");
            buttonType = "Answer";

            // Convert letter to text number (A=0, B=1, C=2, D=3)
            char letterPart = parts[1].charAt(0);
            answerNumber = letterPart - 'A';

            // Extract question number
            questionNumber = Integer.parseInt(parts[2]);
        } else {
            buttonType = "Invalid";
            questionNumber = -1;
            answerNumber = -1;
        }
    }
}