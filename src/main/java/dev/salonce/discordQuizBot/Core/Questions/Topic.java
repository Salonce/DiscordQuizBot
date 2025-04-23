package dev.salonce.discordQuizBot.Core.Questions;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Topic {
    private String name;
    private List<DifficultyLevel> difficulties = new ArrayList<>();

    public Topic(String name, List<RawQuestion> sortedRawQuestions) {
        this.name = name;
        while (!sortedRawQuestions.isEmpty()) {
            difficulties.add(new DifficultyLevel(sortedRawQuestions));
        }
    }

    public boolean difficultyLevelExists(int level) {
        if (difficulties.size() >= level)
            return true;
        return false;
    }

    public DifficultyLevel getDifficultyLevel(int level) {
        return difficulties.get(level - 1);
    }
}
