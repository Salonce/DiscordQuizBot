package dev.salonce.discordQuizBot.Core.Matches;

import dev.salonce.discordQuizBot.Configs.QuizConfig;
import dev.salonce.discordQuizBot.Core.Questions.Question;
import dev.salonce.discordQuizBot.Core.Questions.QuestionListFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class MatchFactory {

    private final QuizConfig quizConfig;
    private final QuestionListFactory questionListFactory;

    public Match makeMatch(String topic, int difficulty, Long ownerId){
        List<Question> questions = questionListFactory.generateMixedDifficultyQuestions(topic, difficulty, quizConfig.getNoOfQuestions());
        return new Match(questions, topic, difficulty, ownerId, quizConfig.getUnansweredLimit());
    }
}
