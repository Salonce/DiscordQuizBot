package dev.salonce.discordquizbot.application;

import dev.salonce.discordquizbot.domain.Question;
import dev.salonce.discordquizbot.domain.Option;
import dev.salonce.discordquizbot.domain.Questions;
import dev.salonce.discordquizbot.infrastructure.configs.QuizSetupConfig;
import dev.salonce.discordquizbot.infrastructure.dtos.RawQuestion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuestionsService {

    private final QuizSetupConfig quizSetupConfig;
    private final RawQuestionsService rawQuestionsService;
    private final Random rand = new Random();

    public boolean doesQuestionSetExist(String topic, int level){
        return rawQuestionsService.doesQuestionSetExist(topic, level);
    }

    private Question create(RawQuestion rawQuestion) {
        List<Option> options = new ArrayList<>();
        Random rand = new Random();

        // pick one correct answer
        int correctIndex = rand.nextInt(rawQuestion.correctAnswers().size());
        options.add(new Option(rawQuestion.correctAnswers().get(correctIndex), true));

        // pick incorrect answers
        Set<Integer> set = new HashSet<>();
        int size = Math.min(3, rawQuestion.incorrectAnswers().size());
        while (set.size() != size){
            set.add(rand.nextInt(rawQuestion.incorrectAnswers().size()));
        }
        for (int i : set) options.add(new Option(rawQuestion.incorrectAnswers().get(i), false));

        Collections.shuffle(options);
        return new Question(rawQuestion.question(), options, rawQuestion.explanation());
    }


    public Questions generateQuestions(String tag, int difficulty){
        int NoQuestions = quizSetupConfig.getQuestionsCount();
        List<Question> list = new ArrayList<>();
        if (difficulty == 1)
            list.addAll(generateExactDifficultyQuestions(tag, difficulty, NoQuestions));
        else{
            int NoQuestionsEasier = NoQuestions/2;
            int NoQuestionsExact = NoQuestions - NoQuestionsEasier;
            list.addAll(generateLowerDifficultyQuestions(tag, difficulty, NoQuestionsEasier));
            list.addAll(generateExactDifficultyQuestions(tag, difficulty, NoQuestionsExact));
        }
        return new Questions(list);
    }

    private List<Question> generateExactDifficultyQuestions(String tag, int difficulty, int NoQuestions){
        List<RawQuestion> rawQuestions = rawQuestionsService.getRawQuestionList(tag, difficulty);
        List<Question> questions = new ArrayList<>();
        if (rawQuestions.size() < NoQuestions)
            log.debug("Not enough questions in category {}.", tag);
        for(int i = 0; i < NoQuestions; i++){
            int next = rand.nextInt(rawQuestions.size());
            questions.add(create(rawQuestions.get(next)));
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
            log.debug("Not enough questions in category {}.", tag);
        for(int i = 0; i < NoQuestions; i++){
            int next = rand.nextInt(rawQuestions.size());
            questions.add(create(rawQuestions.get(next)));
            rawQuestions.remove(next);
        }
        return questions;
    }
}
