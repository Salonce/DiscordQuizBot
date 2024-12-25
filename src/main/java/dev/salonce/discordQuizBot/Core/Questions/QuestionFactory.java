package dev.salonce.discordQuizBot.Core.Questions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class QuestionFactory {

    private final Random rand = new Random();
    private final QuestionRepository questionRepository;

    //add logic to eliminate <5 questions by throwing exceptions
    public List<Question> generateQuestions(String type, int NoQuestions){
        List<RawQuestion> rawQuestions = questionRepository.getQuestions(type);
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
