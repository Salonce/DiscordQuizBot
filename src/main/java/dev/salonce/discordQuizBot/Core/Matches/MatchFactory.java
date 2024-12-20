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

    public Match makeMatch(String type){
        List<Question> questions = questionFactory.generateQuestions(type);
        return new Match(questions, type);
    }

//    public Match javaMatch(){
//        List<Question> questions = questionFactory.generateQuestions("java");
//        return new Match(questions);
//    }
//
//    public Match memoryMatch(){
//        List<Question> questions = questionFactory.generateQuestions("memory");
//        return new Match(questions);
//    }
}
