package dev.salonce.discordquizbot.infrastructure.buttonhandlers;

import dev.salonce.discordquizbot.application.MatchService;
import dev.salonce.discordquizbot.application.ButtonHandler;
import dev.salonce.discordquizbot.application.ResultStatus;
import dev.salonce.discordquizbot.domain.Answer;
import dev.salonce.discordquizbot.infrastructure.dtos.ButtonInteraction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.lang.Integer.parseInt;

@RequiredArgsConstructor
@Component("ButtonAnswer")
public class AnswerButtonHandler implements ButtonHandler {

    private final MatchService matchService;

    @Override
    public Optional<ResultStatus> handle(ButtonInteraction buttonInteraction) {
        String buttonId = buttonInteraction.buttonId();
        if (!buttonId.startsWith("Answer") || !buttonId.matches("Answer-[A-D]-\\d+"))
            return Optional.empty();

        String[] answerData = buttonId.split("-");
        int questionNumber = Integer.parseInt(answerData[2]);
        Answer answer = Answer.fromChar(answerData[1].charAt(0));

        return Optional.of(matchService.addPlayerAnswer(buttonInteraction.channelId(), buttonInteraction.userId(), questionNumber, answer));
    }
}
