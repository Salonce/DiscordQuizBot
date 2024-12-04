package dev.salonce.discordQuizBot.Core.Matches;

import dev.salonce.discordQuizBot.Core.Questions.Question;
import dev.salonce.discordQuizBot.Core.Questions.QuestionFactory;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class MatchFactory {

    private final QuestionFactory questionFactory;

    public Match javaMatch(List<Player> players, MessageChannel messageChannel){
        List<Question> questions = questionFactory.javaQuestions();
        return new Match(questions);
    }

}
