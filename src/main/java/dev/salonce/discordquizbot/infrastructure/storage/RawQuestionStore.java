package dev.salonce.discordquizbot.infrastructure.storage;

import dev.salonce.discordquizbot.infrastructure.dtos.RawQuestion;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Slf4j
@Component
public class RawQuestionStore {

    private final List<RawQuestion> rawQuestions;

    public RawQuestionStore(RawQuestionLoader rawQuestionLoader){
        this.rawQuestions = rawQuestionLoader.loadQuestionsFromResources();
    }
}