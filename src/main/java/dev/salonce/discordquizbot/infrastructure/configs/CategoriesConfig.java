package dev.salonce.discordquizbot.infrastructure.configs;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.*;

@Setter
@Getter
@ConfigurationProperties(prefix = "questions")
@Component
public class CategoriesConfig {

    private Map<String, Set<String>> availableTopics = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(CategoriesConfig.class);

    @PostConstruct
    private void postConstruct() {
        String topics = String.join(", ", availableTopics.keySet());
        log.info("Topics available: {}", topics);
    }
}
