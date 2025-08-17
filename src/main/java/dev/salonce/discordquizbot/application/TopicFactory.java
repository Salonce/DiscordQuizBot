package dev.salonce.discordquizbot.application;

import dev.salonce.discordquizbot.domain.DifficultyLevel;
import dev.salonce.discordquizbot.domain.Topic;
import dev.salonce.discordquizbot.infrastructure.dtos.RawQuestion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TopicFactory {

    public static Topic createTopic(String name, List<RawQuestion> rawQuestions) {
        rawQuestions.sort(Comparator
                .comparing(RawQuestion::difficulty, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(RawQuestion::id, Comparator.nullsLast(Long::compareTo)));

        List<DifficultyLevel> difficulties = new ArrayList<>();
        List<RawQuestion> remaining = new ArrayList<>(rawQuestions);
        while (!remaining.isEmpty()) {
            difficulties.add(new DifficultyLevel(remaining));
        }

        return new Topic(name, difficulties);
    }
}