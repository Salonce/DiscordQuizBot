package dev.salonce.discordquizbot.core.questions.rawquestions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.*;

@Getter
public class RawQuestion {

    private final Long id;
    private final String question;
    private final List<String> correctAnswers;
    private final List<String> incorrectAnswers;
    private final String explanation;
    private final Integer difficulty;
    private final Set<String> tags;

    public RawQuestion(@JsonProperty("id") Long id, @JsonProperty("question") String question, @JsonProperty("correctAnswers") List<String> correctAnswers, @JsonProperty("incorrectAnswers") List<String> incorrectAnswers, @JsonProperty("explanation") String explanation, @JsonProperty("difficulty") Integer difficulty, @JsonProperty("tags") Set<String> tags) {

        this.id = id;
        this.question = question;
        this.correctAnswers = correctAnswers;
        this.incorrectAnswers = incorrectAnswers;
        this.explanation = explanation;
        this.difficulty = difficulty;
        this.tags = tags;
    }

    public boolean containsTag(String tag){
        if (tags == null) {
            System.out.println("Missing or null tags for question ID: " + id);
            System.out.println("question: " + question);
        }
        return tags.contains(tag);
    }
}

