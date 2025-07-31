package dev.salonce.discordquizbot.core.questions.questions;

import dev.salonce.discordquizbot.core.questions.topics.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class QuestionsService {

    private final QuestionsGenerator questionsGenerator;
    private final TopicService topicService;

    public List<Question> generateQuestions(String tag, int difficulty, int NoQuestions){
        return questionsGenerator.generateQuestions(tag, difficulty, NoQuestions);
    }

    public boolean doesQuestionSetExist(String topic, int level){
        return topicService.doesQuestionSetExist(topic, level);
    }
}
