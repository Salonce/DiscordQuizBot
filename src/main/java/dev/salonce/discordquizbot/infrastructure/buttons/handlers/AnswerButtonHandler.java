package dev.salonce.discordquizbot.infrastructure.buttons.handlers;

import dev.salonce.discordquizbot.domain.matches.Match;
import dev.salonce.discordquizbot.domain.matches.MatchService;
import dev.salonce.discordquizbot.domain.matches.MatchState;
import dev.salonce.discordquizbot.infrastructure.buttons.ButtonHandler;
import dev.salonce.discordquizbot.infrastructure.buttons.ButtonInteractionData;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.Integer.parseInt;

@RequiredArgsConstructor
@Component("ButtonAnswer")
public class AnswerButtonHandler implements ButtonHandler {

    private final MatchService matchService;

    @Override
    public boolean handle(ButtonInteractionEvent event, ButtonInteractionData buttonInteractionData) {
        String buttonId = buttonInteractionData.getButtonId();
        if (!buttonId.startsWith("Answer") || !buttonId.matches("Answer-[A-D]-\\d+"))
            return false;

        String[] answerData = buttonId.split("-");
        int questionNumber = getQuestionNumber(answerData);
        int answerNumber = getAnswerNumber(answerData);

        String response = getPlayerAnswer(buttonInteractionData, questionNumber, answerNumber);

        event.reply(response)
                .withEphemeral(true)
                .subscribe();
        return true;
    }

    private int getAnswerNumber(String[] answerData){
        return answerData[1].charAt(0) - 'A';
    }

    private int getQuestionNumber(String[] answerData){
        return Integer.parseInt(answerData[2]);
    }

    private String getPlayerAnswer(ButtonInteractionData buttonInteractionData, int questionNumber, int answerNumber) {
        Long userId = buttonInteractionData.getUserId();
        Match match = matchService.get(buttonInteractionData.getMessageChannel());

        if (match == null || questionNumber != match.getCurrentQuestionNum() || match.getMatchState() != MatchState.ANSWERING)
            return "Your answer came too late!";

        if (!match.getPlayers().containsKey(userId))
            return "You are not in the match.";

        List<Integer> answers = match.getPlayers().get(userId).getAnswersList();
        answers.set(questionNumber, answerNumber);
        return "Your answer: " + (char) ('A' + answerNumber) + ".";

    }
}
