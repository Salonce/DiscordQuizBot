package dev.salonce.discordquizbot.core.questions.questions;

import dev.salonce.discordquizbot.core.questions.rawquestions.RawQuestion;
import dev.salonce.discordquizbot.core.questions.topics.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class QuestionsGenerator {

    private final Random rand = new Random();
    private final TopicService topicService;


    private List<Question> generateExactDifficultyQuestions(String tag, int difficulty, int NoQuestions){
        List<RawQuestion> rawQuestions = topicService.getRawQuestionList(tag, difficulty);
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
            rawQuestions.addAll(topicService.getRawQuestionList(tag, i));
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


    List<Question> generateQuestions(String tag, int difficulty, int NoQuestions){
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
}
