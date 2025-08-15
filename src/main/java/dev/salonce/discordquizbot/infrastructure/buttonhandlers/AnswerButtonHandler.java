package dev.salonce.discordquizbot.infrastructure.buttonhandlers;

import dev.salonce.discordquizbot.application.MatchService;
import dev.salonce.discordquizbot.application.ButtonHandler;
import dev.salonce.discordquizbot.infrastructure.dtos.ButtonInteraction;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static java.lang.Integer.parseInt;

@RequiredArgsConstructor
@Component("ButtonAnswer")
public class AnswerButtonHandler implements ButtonHandler {

    private final MatchService matchService;

    @Override
    public boolean handle(ButtonInteractionEvent event, ButtonInteraction buttonInteraction) {
        String buttonId = buttonInteraction.buttonId();
        if (!buttonId.startsWith("Answer") || !buttonId.matches("Answer-[A-D]-\\d+"))
            return false;

        String[] answerData = buttonId.split("-");
        int questionNumber = Integer.parseInt(answerData[2]);
        int answerNumber = answerData[1].charAt(0) - 'A';

        String response = matchService.getPlayerAnswer(buttonInteraction.channelId(), buttonInteraction.userId(), questionNumber, answerNumber);

        event.reply(response)
                .withEphemeral(true)
                .subscribe();
        return true;
    }
}
