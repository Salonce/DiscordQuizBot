package dev.salonce.discordquizbot.infrastructure;

import dev.salonce.discordquizbot.infrastructure.dtos.RawQuestion;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class RawQuestionStore {

    private final RawQuestionLoader rawQuestionLoader;
    private List<RawQuestion> rawQuestions;

    @PostConstruct
    public void init(){
        rawQuestions = rawQuestionLoader.loadQuestionsFromResources();
    }

    public List<RawQuestion> getAllRawQuestions(){
        return new ArrayList<>(rawQuestions);
    }

    public List<RawQuestion> getRawQuestions(Set<String> tags){
        Set<RawQuestion> rawQuestions = new HashSet<>();
        for (RawQuestion rawQuestion : this.rawQuestions){
            for (String tag : tags){
                if (rawQuestion.tags() == null) {
                    System.out.println("Missing or null tags for question ID: " + rawQuestion.id());
                    System.out.println("question: " + rawQuestion.question());
                    break;
                }
                if (rawQuestion.tags().contains(tag)) {
                    rawQuestions.add(rawQuestion);
                    break;
                }
            }
        }
        return new ArrayList<>(rawQuestions);
    }
}