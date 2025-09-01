package dev.salonce.discordquizbot.application;

import dev.salonce.discordquizbot.domain.Categories;
import dev.salonce.discordquizbot.domain.Category;
import dev.salonce.discordquizbot.domain.DifficultyLevel;
import dev.salonce.discordquizbot.infrastructure.configs.CategoriesConfig;
import dev.salonce.discordquizbot.infrastructure.storage.RawQuestionStore;
import dev.salonce.discordquizbot.infrastructure.dtos.RawQuestion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RawQuestionsService {

    private final RawQuestionStore rawQuestionStore;
    private final CategoriesConfig categoriesConfig;

    public Categories createCategories(){
        Categories categories = new Categories();
        for (Map.Entry<String, Set<String>> entry : categoriesConfig.getAvailableTopics().entrySet()) {
            String topicName = entry.getKey();
            Set<String> tagsSet = entry.getValue();
            List<RawQuestion> rawTopicQuestions = getRawQuestionsForTags(tagsSet);
            List<DifficultyLevel> difficultyLevels = prepareDifficultyLevels(rawTopicQuestions);
            Category category = new Category(topicName, difficultyLevels);
            categories.addTopic(topicName, category);
        }
        return categories;
    }

    private List<RawQuestion> removePrepareQuestionsForDifficultyLevel(List<RawQuestion> removableRawQuestions) {
        List<RawQuestion> preparedRawQuestions = new ArrayList<>();
        if (removableRawQuestions.size() < 65) {
            int size = removableRawQuestions.size();
            for (int i = 0; i < size; i++) {
                preparedRawQuestions.add(removableRawQuestions.get(0));
                removableRawQuestions.remove(0);
            }
        } else {
            for (int i = 0; i < 50; i++) {
                preparedRawQuestions.add(removableRawQuestions.get(0));
                removableRawQuestions.remove(0);
            }
        }
        return preparedRawQuestions;
    }

    private List<DifficultyLevel> prepareDifficultyLevels(List<RawQuestion> rawQuestions) {
        rawQuestions.sort(Comparator
                .comparing(RawQuestion::difficulty, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(RawQuestion::id, Comparator.nullsLast(Long::compareTo)));

        List<DifficultyLevel> difficulties = new ArrayList<>();
        List<RawQuestion> remaining = new ArrayList<>(rawQuestions);

        while (!remaining.isEmpty()) {
            List<RawQuestion> prepared = removePrepareQuestionsForDifficultyLevel(remaining);
            difficulties.add(new DifficultyLevel(prepared));
        }
        return difficulties;
    }

    private List<RawQuestion> getRawQuestionsForTags(Set<String> topicTags) {
        return rawQuestionStore.getRawQuestions().stream()
                .filter(rawQuestion -> {
                    Set<String> rawQuestionTags = rawQuestion.tags();
                    if (rawQuestionTags == null) {
                        log.warn("Missing or null tags for question: ID: {}, question: {}", rawQuestion.id(), rawQuestion.question());
                        return false;
                    }
                    return !Collections.disjoint(rawQuestionTags, topicTags);
                })
                .distinct()
                .collect(Collectors.toList());
    }
}
