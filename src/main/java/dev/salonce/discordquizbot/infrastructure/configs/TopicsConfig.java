package dev.salonce.discordquizbot.infrastructure.configs;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.*;

@Setter
@Getter
@ConfigurationProperties(prefix = "questions")
@Component
public class TopicsConfig {

    private Map<String, Set<String>> availableTopics = new HashMap<>();

    @PostConstruct
    private void postConst(){
        for (String string : availableTopics.keySet())
            System.out.println(string);
    }
}
