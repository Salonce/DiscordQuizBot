package dev.salonce.discordquizbot.application;

import dev.salonce.discordquizbot.domain.DifficultyLevel;
import dev.salonce.discordquizbot.domain.Topic;
import dev.salonce.discordquizbot.infrastructure.dtos.RawQuestion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Component
public class TopicFactory {

    private final RawQuestionsService rawQuestionsService;

    public Topic createTopic(String name, List<RawQuestion> rawQuestions) {
        rawQuestions.sort(Comparator
                .comparing(RawQuestion::difficulty, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(RawQuestion::id, Comparator.nullsLast(Long::compareTo)));

        List<DifficultyLevel> difficulties = new ArrayList<>();
        List<RawQuestion> remainingRawQuestions = new ArrayList<>(rawQuestions);
        while (!remainingRawQuestions.isEmpty()) {
            List<RawQuestion> preparedRawQuestions = rawQuestionsService.removePrepareQuestionsForDifficultyLevel(remainingRawQuestions);
            difficulties.add(new DifficultyLevel(preparedRawQuestions));


        }

        return new Topic(name, difficulties);
    }
}