package dev.salonce.discordquizbot.domain;

import lombok.Getter;
import java.util.List;

@Getter
public class Topic {
    private final String name;
    private final List<DifficultyLevel> difficulties;

    public Topic(String name, List<DifficultyLevel> difficulties) {
        this.name = name;
        this.difficulties = List.copyOf(difficulties); // immutable copy
    }

    public boolean difficultyLevelExists(int level) {
        return difficulties.size() >= level;
    }

    public DifficultyLevel getDifficultyLevel(int level) {
        return difficulties.get(level - 1);
    }
}