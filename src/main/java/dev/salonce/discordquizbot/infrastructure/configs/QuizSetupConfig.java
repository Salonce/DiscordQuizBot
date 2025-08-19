package dev.salonce.discordquizbot.infrastructure.configs;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
@Getter
public class QuizSetupConfig {

    private final int noOfQuestions;
    private final int maxInactivity;
    private final int timeToJoinQuiz;
    private final int timeToStartMatch;
    private final int timeToPickAnswer;
    private final int timeForNewQuestionToAppear;

    public QuizSetupConfig(@Value("${mode}") String mode) {
        switch (mode) {
            case "testing" -> {
                this.noOfQuestions = 4;
                this.maxInactivity = 3;
                this.timeToJoinQuiz = 7;
                this.timeToStartMatch = 2;
                this.timeToPickAnswer = 5;
                this.timeForNewQuestionToAppear = 1;
            }
            case "standard" -> {
                this.noOfQuestions = 10;
                this.maxInactivity = 3;
                this.timeToJoinQuiz = 30;
                this.timeToStartMatch = 3;
                this.timeToPickAnswer = 30;
                this.timeForNewQuestionToAppear = 7;
            }
            default -> throw new IllegalArgumentException(
                    "Invalid mode '" + mode + "' in application.yaml! Valid values: 'testing', 'standard'."
            );
        }
    }

    @PostConstruct
    public void timers() {
        System.out.println("Set timers:");
        System.out.println("timeToJoinQuiz: " + timeToJoinQuiz +
                "\ntimeForQuizToStart: " + timeToStartMatch +
                "\ntimeToAnswerQuestion: " + timeToPickAnswer +
                "\ntimeForNewQuestionToAppear: " + timeForNewQuestionToAppear);
    }
}