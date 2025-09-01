package dev.salonce.discordquizbot.infrastructure.storage;

import dev.salonce.discordquizbot.infrastructure.dtos.RawQuestion;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RawQuestionStore {

    private final RawQuestionLoader rawQuestionLoader;
    private List<RawQuestion> rawQuestions;

    @PostConstruct
    public void init(){
        rawQuestions = rawQuestionLoader.loadQuestionsFromResources();
    }

    public List<RawQuestion> getRawQuestions(Set<String> topicTags) {
        return rawQuestions.stream()
            .filter(rawQuestion -> {
                Set<String> rawQuestionTags = rawQuestion.tags();
                if (rawQuestionTags == null) {
                    log.debug("Missing or null tags for question: ID: {}, question: {}", rawQuestion.id(), rawQuestion.question());
                    return false;
                }
                return !Collections.disjoint(rawQuestionTags, topicTags);
            })
            .distinct()
            .collect(Collectors.toList());
    }
}