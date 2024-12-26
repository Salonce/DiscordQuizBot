package dev.salonce.discordQuizBot.Configs;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "questions")
public class QuestionSetsConfig {

    private Map<String, List<String>> files;

    @PostConstruct
    public void logFiles() {
        files.forEach((key, value) ->{
            for (String path : value){
                System.out.println("Loaded question strings, type: " + key + ", path: " + path);
            }
        });
    }
}