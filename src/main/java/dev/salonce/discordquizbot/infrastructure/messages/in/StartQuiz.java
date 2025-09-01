package dev.salonce.discordquizbot.infrastructure.messages.in;

import dev.salonce.discordquizbot.infrastructure.dtos.DiscordMessage;
import dev.salonce.discordquizbot.application.MatchService;
import dev.salonce.discordquizbot.application.QuestionsService;
import dev.salonce.discordquizbot.application.QuizFlowService;
import dev.salonce.discordquizbot.application.MessageHandler;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("startQuiz")
@RequiredArgsConstructor
public class StartQuiz implements MessageHandler {
    private final MatchService matchService;
    private final QuizFlowService quizFlowService;
    private final QuestionsService questionsService;

    @Override
    public boolean handleMessage(DiscordMessage discordMessage) {
        String[] message = discordMessage.content().split(" ");

        if (!(message[0].equals("qq") && (message[1].equals("quiz") || message[1].equals("start") || message[1].equals("play"))))
            return false;

        if (message.length < 4 || message.length > 6)
            return true; // command is of wrong length, end the chain

        int difficulty;
        try{
            difficulty = Integer.parseInt(message[message.length-1]);
        } catch (NumberFormatException e) {
            return true; // end the chain - wrong integer, perhaps send a message that it is wrong
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 2; i < message.length - 1; i++) {
            sb.append(message[i]);
            if (i != message.length - 2)
                sb.append(" ");
        }
        String topic = sb.toString();
        if (!questionsService.doesQuestionSetExist(topic, difficulty))
            return true;

        Long userId = discordMessage.userId();
        MessageChannel messageChannel = discordMessage.channel();
        messageChannel.getId();

        log.info("User {} started a match: {} {}", userId, topic, difficulty);
        quizFlowService.startMatch(messageChannel, topic, difficulty, userId);
        return true;
    }
}
