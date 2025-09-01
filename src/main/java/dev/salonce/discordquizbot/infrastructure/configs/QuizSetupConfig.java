package dev.salonce.discordquizbot.infrastructure.configs;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
@Getter
public class QuizSetupConfig {

    private static final Logger log = LoggerFactory.getLogger(QuizSetupConfig.class);
    private final int questionsCount;
    private final int maxInactivityCount;
    private final int joinTimeoutSeconds;
    private final int matchStartDelaySeconds;
    private final int answerTimeoutSeconds;
    private final int nextQuestionDelaySeconds;

    public QuizSetupConfig(@Value("${mode}") String mode) {
        switch (mode) {
            case "testing" -> {
                this.questionsCount = 3;
                this.maxInactivityCount = 1;
                this.joinTimeoutSeconds = 10;
                this.matchStartDelaySeconds = 2;
                this.answerTimeoutSeconds = 10;
                this.nextQuestionDelaySeconds = 1;
            }
            case "standard" -> {
                this.questionsCount = 10;
                this.maxInactivityCount = 3;
                this.joinTimeoutSeconds = 30;
                this.matchStartDelaySeconds = 3;
                this.answerTimeoutSeconds = 30;
                this.nextQuestionDelaySeconds = 7;
            }
            default -> throw new IllegalArgumentException(
                    "Invalid mode '" + mode + "' in application.yaml! Valid values: 'testing', 'standard'."
            );
        }
    }

    @PostConstruct
    public void timers() {
        log.info("Quiz configuration: Number of questions per quiz: {}, Maximum users' inactivity: {}, Timeout for joining (seconds): {}, Delay for starting the match (seconds): {}, Timeout for answering (seconds): {}, Delay between questions (seconds): {}",
                        questionsCount, maxInactivityCount, joinTimeoutSeconds, matchStartDelaySeconds,  answerTimeoutSeconds, nextQuestionDelaySeconds);
    }
}