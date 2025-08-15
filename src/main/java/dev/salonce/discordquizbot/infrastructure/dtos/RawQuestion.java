package dev.salonce.discordquizbot.infrastructure.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.*;

public record RawQuestion(@JsonProperty("id") Long id,
                          @JsonProperty("question") String question,
                          @JsonProperty("correctAnswers") List<String> correctAnswers,
                          @JsonProperty("incorrectAnswers") List<String> incorrectAnswers,
                          @JsonProperty("explanation") String explanation,
                          @JsonProperty("difficulty") Integer difficulty,
                          @JsonProperty("tags") Set<String> tags) {
}