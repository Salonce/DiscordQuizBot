package dev.salonce.discordQuizBot.Core.Matches;

import dev.salonce.discordQuizBot.Core.Questions.Question;
import dev.salonce.discordQuizBot.Core.Questions.QuestionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class MatchFactory {

    private final QuestionFactory questionFactory;

    public Match javaMatch(){
        List<Question> questions = questionFactory.javaQuestions();
        //System.out.println(questions.get(0).getQuestion());
        return new Match(questions);
    }

}
