package dev.salonce.discordquizbot.infrastructure.configs;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public final class ApplicationYmlConfig {
    public static void configure(){
        Resource primaryResource = new ClassPathResource("private/application.yml");
        String configLocation = primaryResource.exists() ?
                "classpath:/private/application.yml" :
                "classpath:/sample/application-sample.yml";
        System.setProperty("spring.config.location", configLocation);
    }
}
