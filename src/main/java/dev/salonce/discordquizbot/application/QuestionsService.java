package dev.salonce.discordquizbot.application;

import dev.salonce.discordquizbot.domain.Question;
import dev.salonce.discordquizbot.infrastructure.dtos.RawQuestion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class QuestionsService {

    private final RawQuestionsService rawQuestionsService;
    private final Random rand = new Random();

    public boolean doesQuestionSetExist(String topic, int level){
        return rawQuestionsService.doesQuestionSetExist(topic, level);
    }

    public List<Question> generateQuestions(String tag, int difficulty, int NoQuestions){
        List<Question> questions = new ArrayList<>();
        if (difficulty == 1)
            questions.addAll(generateExactDifficultyQuestions(tag, difficulty, NoQuestions));
        else{
            int NoQuestionsEasier = NoQuestions/2;
            int NoQuestionsExact = NoQuestions - NoQuestionsEasier;
            questions.addAll(generateLowerDifficultyQuestions(tag, difficulty, NoQuestionsEasier));
            questions.addAll(generateExactDifficultyQuestions(tag, difficulty, NoQuestionsExact));
        }
        return questions;
    }

    private List<Question> generateExactDifficultyQuestions(String tag, int difficulty, int NoQuestions){
        List<RawQuestion> rawQuestions = rawQuestionsService.getRawQuestionList(tag, difficulty);
        List<Question> questions = new ArrayList<>();
        if (rawQuestions.size() < NoQuestions)
            System.out.println("Not enough questions in this category...");
        for(int i = 0; i < NoQuestions; i++){
            int next = rand.nextInt(rawQuestions.size());
            questions.add(new Question(rawQuestions.get(next)));
            rawQuestions.remove(next);
        }
        return questions;
    }

    private List<Question> generateLowerDifficultyQuestions(String tag, int difficulty, int NoQuestions){
        List<RawQuestion> rawQuestions = new ArrayList<>();
        for (int i = 1; i < difficulty; i++){
            rawQuestions.addAll(rawQuestionsService.getRawQuestionList(tag, i));
        }
        List<Question> questions = new ArrayList<>();
        if (rawQuestions.size() < NoQuestions)
            System.out.println("Not enough questions in this category...");
        for(int i = 0; i < NoQuestions; i++){
            int next = rand.nextInt(rawQuestions.size());
            questions.add(new Question(rawQuestions.get(next)));
            rawQuestions.remove(next);
        }
        return questions;
    }
}
