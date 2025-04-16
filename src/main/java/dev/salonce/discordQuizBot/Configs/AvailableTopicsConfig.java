package dev.salonce.discordQuizBot.Configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.*;

@Setter
@Getter
@ConfigurationProperties(prefix = "questions")
@Component
public class AvailableTopicsConfig {

    private Map<String, List<Integer>> availableTopics = new HashMap<>();
}
