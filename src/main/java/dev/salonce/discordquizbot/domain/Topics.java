package dev.salonce.discordquizbot.domain;

import dev.salonce.discordquizbot.infrastructure.dtos.RawQuestion;

import java.util.*;

public class Topics {

    private final Map<String, Topic> topicsMap = new HashMap<>();

    public Map<String, Topic> getAsMap(){
        return topicsMap;
    }

    public Topic getFirstTopic(){
        Iterator<Topic> iterator = topicsMap.values().iterator();

        if (iterator.hasNext()) {
            return iterator.next();
        }
        else
            return null;
    }

    public Topic getSecondTopic(){
        Iterator<Topic> iterator = topicsMap.values().iterator();

        if (iterator.hasNext()) {
            iterator.next();
            if (iterator.hasNext())
                return iterator.next();
        }
        return null;
    }

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

    public boolean areNone(){
        return topicsMap.isEmpty();
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
