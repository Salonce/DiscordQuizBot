package dev.salonce.discordquizbot.domain;

import dev.salonce.discordquizbot.infrastructure.dtos.RawQuestion;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Getter
public class DifficultyLevel {

    public DifficultyLevel(List<RawQuestion> rawQuestions){
        this.rawQuestions = rawQuestions;
    }

    public List<RawQuestion> rawQuestions = new ArrayList<>();
}
