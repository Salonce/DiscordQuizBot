package dev.salonce.discordquizbot.domain;

import dev.salonce.discordquizbot.infrastructure.dtos.RawQuestion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Topics {

    private final Map<String, Topic> topicsMap = new HashMap<>();

    public void addTopic(String topicName, Topic topic){
        topicsMap.put(topicName, topic);
    }

    public boolean doesQuestionSetExist(String topic, int level){
        if (!topicsMap.containsKey(topic))
            return false;
        if (!topicsMap.get(topic).difficultyLevelExists(level))
            return false;
        return true;
    }

    public List<RawQuestion> getRawQuestionsForTopicDifficultyLevel(String topic, int level){
        if (!doesQuestionSetExist(topic, level))
            return null;
        return new ArrayList<>(topicsMap.get(topic).getDifficultyLevel(level).rawQuestions());
    }

    public List<RawQuestion> getRawQuestionList(String topic, int level){
        if (!doesQuestionSetExist(topic, level))
            return null;
        return new ArrayList<>(topicsMap.get(topic).getDifficultyLevel(level).rawQuestions());
    }
}
