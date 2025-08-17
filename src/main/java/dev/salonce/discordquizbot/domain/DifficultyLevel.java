package dev.salonce.discordquizbot.domain;

import dev.salonce.discordquizbot.infrastructure.dtos.RawQuestion;
import java.util.List;

public record DifficultyLevel(List<RawQuestion> rawQuestions) {}
