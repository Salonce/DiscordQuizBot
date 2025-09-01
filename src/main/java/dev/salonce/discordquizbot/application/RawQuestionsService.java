package dev.salonce.discordquizbot.application;

import dev.salonce.discordquizbot.domain.DifficultyLevel;
import dev.salonce.discordquizbot.domain.Topic;
import dev.salonce.discordquizbot.infrastructure.storage.RawQuestionStore;
import dev.salonce.discordquizbot.infrastructure.configs.TopicsConfig;
import dev.salonce.discordquizbot.infrastructure.dtos.RawQuestion;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class RawQuestionsService {

    private final TopicsConfig topicsConfig;
    private final RawQuestionStore rawQuestionStore;

    @Getter
    private final Map<String, Topic> topicsMap = new HashMap<>();

    @PostConstruct
    public void init(){
        for (Map.Entry<String, Set<String>> entry : topicsConfig.getAvailableTopics().entrySet()){
            String topicName = entry.getKey();
            Set<String> tagsSet = entry.getValue();
            List<RawQuestion> rawTopicQuestions = getRawQuestionsForTags(tagsSet);
            List<DifficultyLevel> difficultyLevels = prepareDifficultyLevels(rawTopicQuestions);
            Topic topic = new Topic(topicName, difficultyLevels);
            topicsMap.put(topicName, topic);
        }
    }

    public boolean doesQuestionSetExist(String topic, int level){
        if (!topicsMap.containsKey(topic))
            return false;
        if (!topicsMap.get(topic).difficultyLevelExists(level))
            return false;
        return true;
    }

    public List<RawQuestion> getRawQuestionList(String topic, int level){
        if (!doesQuestionSetExist(topic, level))
            return null;
        return new ArrayList<>(topicsMap.get(topic).getDifficultyLevel(level).rawQuestions());
    }

    public List<RawQuestion> removePrepareQuestionsForDifficultyLevel(List<RawQuestion> removableRawQuestions) {
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
