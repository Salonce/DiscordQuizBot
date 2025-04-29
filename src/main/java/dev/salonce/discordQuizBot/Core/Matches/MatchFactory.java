package dev.salonce.discordQuizBot.Core.Matches;

import dev.salonce.discordQuizBot.Configs.TimersConfig;
import dev.salonce.discordQuizBot.Core.Questions.Question;
import dev.salonce.discordQuizBot.Core.Questions.QuestionListFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class MatchFactory {

    private final TimersConfig timersConfig;
    private final QuestionListFactory questionListFactory;

    public Match makeMatch(String topic, int difficulty, Long ownerId){
        List<Question> questions = questionListFactory.generateMixedDifficultyQuestions(topic, difficulty, timersConfig.getNoOfQuestions());
        return new Match(questions, topic, difficulty, ownerId, timersConfig.getUnansweredLimit());
    }
}
