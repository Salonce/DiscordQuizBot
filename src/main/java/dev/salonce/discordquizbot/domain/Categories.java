package dev.salonce.discordquizbot.domain;

import dev.salonce.discordquizbot.infrastructure.dtos.RawQuestion;

import java.util.*;

public class Categories {

    private final Map<String, Category> categoriesMap = new HashMap<>();

    public List<Category> getSortedList(){
        return categoriesMap.values().stream()
                .sorted(Comparator.comparing(Category::getName))
                .toList();
    }

    public Category getFirstCategory(){
        Iterator<Category> iterator = categoriesMap.values().iterator();

        if (iterator.hasNext()) {
            return iterator.next();
        }
        else
            return null;
    }

    public Category getSecondCategory(){
        Iterator<Category> iterator = categoriesMap.values().iterator();

        if (iterator.hasNext()) {
            iterator.next();
            if (iterator.hasNext())
                return iterator.next();
        }
        return null;
    }

    public void addCategory(String categoryName, Category category){
        categoriesMap.put(categoryName, category);
    }

    public boolean doesQuestionSetExist(String category, int level){
        if (!categoriesMap.containsKey(category))
            return false;
        if (!categoriesMap.get(category).difficultyLevelExists(level))
            return false;
        return true;
    }

    public boolean areNone(){
        return categoriesMap.isEmpty();
    }

    public List<RawQuestion> getRawQuestionList(String category, int level){
        if (!doesQuestionSetExist(category, level))
            return null;
        return new ArrayList<>(categoriesMap.get(category).getDifficultyLevel(level).rawQuestions());
    }
}
