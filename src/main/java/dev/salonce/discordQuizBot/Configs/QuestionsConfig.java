package dev.salonce.discordQuizBot.Configs;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "questions")
public class QuestionsConfig {

    private Map<String, String> files;

    @PostConstruct
    public void logFiles() {
        files.forEach((key, value) -> System.out.println("Loaded questions file: type: " + key + ", path: " + value));
    }
}