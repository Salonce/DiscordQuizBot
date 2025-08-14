package dev.salonce.discordquizbot.infrastructure.buttons.handlers;

import dev.salonce.discordquizbot.domain.Match;
import dev.salonce.discordquizbot.application.MatchService;
import dev.salonce.discordquizbot.domain.MatchState;
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
        int questionNumber = answerData[1].charAt(0) - 'A';
        int answerNumber = Integer.parseInt(answerData[2]);

        String response = matchService.getPlayerAnswer(buttonInteractionData.getMessageChannel(), buttonInteractionData.getUserId(), questionNumber, answerNumber);

        event.reply(response)
                .withEphemeral(true)
                .subscribe();
        return true;
    }
}
