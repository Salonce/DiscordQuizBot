package dev.salonce.discordQuizBot.Core.Questions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class QuestionListFactory {

    private final Random rand = new Random();
    private final RawQuestionService rawQuestionService;

    //add logic to eliminate <5 questions by throwing exceptions
    public List<Question> generateQuestions(String tag, int difficulty, int NoQuestions){
        List<RawQuestion> rawQuestions = rawQuestionService.getRawQuestions(tag, difficulty);
        List<Question> questions = new ArrayList<>();
        if (rawQuestions.size() < NoQuestions)
            System.out.println("Not enough questions in this category...");
        for(int i = 0; i < NoQuestions; i++){
            int next = rand.nextInt(rawQuestions.size());
            questions.add(new Question(rawQuestions.get(next)));
            rawQuestions.remove(next);
            //System.out.println(questions.get(i).getQuestion());
        }

        return questions;
    }
}
