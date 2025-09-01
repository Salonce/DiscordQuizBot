package dev.salonce.discordquizbot.domain;

import dev.salonce.discordquizbot.infrastructure.dtos.RawQuestion;

import java.util.*;

public class Categories {

    private final Map<String, Category> categoriesMap = new HashMap<>();

    public Map<String, Category> getAsMap(){
        return categoriesMap;
    }

    public List<Category> getSortedList(){
        return categoriesMap.values().stream()
                .sorted(Comparator.comparing(Category::getName))
                .toList();
    }

    public Category getFirstTopic(){
        Iterator<Category> iterator = categoriesMap.values().iterator();

        if (iterator.hasNext()) {
            return iterator.next();
        }
        else
            return null;
    }

    public Category getSecondTopic(){
        Iterator<Category> iterator = categoriesMap.values().iterator();

        if (iterator.hasNext()) {
            iterator.next();
            if (iterator.hasNext())
                return iterator.next();
        }
        return null;
    }

    public void addTopic(String topicName, Category category){
        categoriesMap.put(topicName, category);
    }

    public boolean doesQuestionSetExist(String topic, int level){
        if (!categoriesMap.containsKey(topic))
            return false;
        if (!categoriesMap.get(topic).difficultyLevelExists(level))
            return false;
        return true;
    }

    public boolean areNone(){
        return categoriesMap.isEmpty();
    }

    public List<RawQuestion> getRawQuestionsForTopicDifficultyLevel(String topic, int level){
        if (!doesQuestionSetExist(topic, level))
            return null;
        return new ArrayList<>(categoriesMap.get(topic).getDifficultyLevel(level).rawQuestions());
    }

    public List<RawQuestion> getRawQuestionList(String topic, int level){
        if (!doesQuestionSetExist(topic, level))
            return null;
        return new ArrayList<>(categoriesMap.get(topic).getDifficultyLevel(level).rawQuestions());
    }
}
