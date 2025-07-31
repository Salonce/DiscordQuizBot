package dev.salonce.discordquizbot.core.questions.questions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class QuestionsService {

    private final QuestionsGenerator questionsGenerator;

    public List<Question> generateQuestions(String tag, int difficulty, int NoQuestions){
        return questionsGenerator.generateQuestions(tag, difficulty, NoQuestions);
    }
}
