package dev.salonce.discordQuizBot.Core.Questions;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ConfigurationProperties(prefix = "questions")
@Component
@Getter
public class AvailableTopicsConfig {
    private final Set<String> availableTopics = new HashSet<>();
}
