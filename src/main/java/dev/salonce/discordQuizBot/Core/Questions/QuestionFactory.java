package dev.salonce.discordQuizBot.Core.Questions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class QuestionFactory {

    private Random rand = new Random();
    private final QuestionRepository questionRepository;

    //add logic to eliminate <5 questions by throwing exceptions
    public List<Question> javaQuestions(){
        List<RawQuestion> rawQuestions = questionRepository.getJavaQuestions();
        List<Question> questions = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            int next = rand.nextInt(rawQuestions.size());
            questions.add(rawQuestions.get(next).generateQuestion());
            rawQuestions.remove(next);
        }

        return questions;
    }
}
