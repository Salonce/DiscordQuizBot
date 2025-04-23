package dev.salonce.discordQuizBot.Core.Questions;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
public class Topic {
    private String name;
    private List<DifficultyLevel> difficulties = new ArrayList<>();

    public Topic(String name, List<RawQuestion> rawQuestions) {
        sortQuestions(rawQuestions);
        this.name = name;
        while (!rawQuestions.isEmpty()) {
            difficulties.add(new DifficultyLevel(rawQuestions));
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

    private void sortQuestions(List<RawQuestion> questions) {
        questions.sort(Comparator
                .comparing(RawQuestion::getDifficulty, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(RawQuestion::getId, Comparator.nullsLast(Long::compareTo)));
    }
}
