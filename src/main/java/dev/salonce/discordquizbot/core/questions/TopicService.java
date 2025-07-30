package dev.salonce.discordquizbot.core.questions;

import dev.salonce.discordquizbot.core.questions.rawquestions.RawQuestion;
import dev.salonce.discordquizbot.core.questions.rawquestions.RawQuestionService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@RequiredArgsConstructor
public class TopicService {

    private final RawQuestionService rawQuestionService;
    private final TopicsConfig topicsConfig;

    @Getter
    private final Map<String, Topic> topicsMap = new HashMap<>();

    @PostConstruct
    public void init(){
        for (Map.Entry<String, Set<String>> entry : topicsConfig.getAvailableTopics().entrySet()){
            String topicName = entry.getKey();
            Set<String> tagsSet = entry.getValue();
            List<RawQuestion> rawTopicQuestions = rawQuestionService.getRawQuestions(tagsSet);
            topicsMap.put(topicName, new Topic(topicName, rawTopicQuestions));
        }
    }

    public boolean doesQuestionSetExist(String topic, int level){
        if (!topicsMap.containsKey(topic))
            return false;
        if (!topicsMap.get(topic).difficultyLevelExists(level))
            return false;
        return true;
    }

    public List<RawQuestion> getRawQuestionList(String topic, int level){
        if (!doesQuestionSetExist(topic, level))
            return null;
        return new ArrayList<>(topicsMap.get(topic).getDifficultyLevel(level).getRawQuestions());
    }
}
