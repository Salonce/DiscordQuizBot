package dev.salonce.discordQuizBot.Core.Matches;

import dev.salonce.discordQuizBot.Configs.QuizConfig;
import dev.salonce.discordQuizBot.Core.Questions.Question;
import dev.salonce.discordQuizBot.Core.Questions.QuestionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class MatchFactory {

    private final QuizConfig quizConfig;
    private final QuestionFactory questionFactory;

    public Match makeMatch(String type, Long ownerId){
        List<Question> questions = questionFactory.generateQuestions(type, quizConfig.getNoOfQuestions());
        return new Match(questions, type, ownerId, quizConfig);
    }
}
