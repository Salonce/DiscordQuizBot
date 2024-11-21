package dev.salonce.discordQuizBot.Core;

import discord4j.core.object.entity.channel.MessageChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Component
public class MatchMaker {

    private final QuestionFactory questionFactory;

    public Match javaMatch(List<Player> players, MessageChannel messageChannel) throws IOException {
        List<Question> questions = questionFactory.javaQuestions();
        return new Match(questions, players, messageChannel);
    }
}
