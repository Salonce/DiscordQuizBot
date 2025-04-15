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
    private final RawQuestionRepository rawQuestionRepository;

    //add logic to eliminate <5 questions by throwing exceptions
    public List<Question> generateQuestions(String tag, int NoQuestions){
        List<RawQuestion> rawQuestions = rawQuestionRepository.getRawQuestions(tag);
        List<Question> questions = new ArrayList<>();
        for(int i = 0; i < NoQuestions; i++){
            int next = rand.nextInt(rawQuestions.size());
            questions.add(rawQuestions.get(next).generateQuestion());
            rawQuestions.remove(next);
            //System.out.println(questions.get(i).getQuestion());
        }

        return questions;
    }
}
