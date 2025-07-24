package dev.salonce.discordquizbot.configs;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
@Getter
public class TimersConfig {

    private final int noOfQuestions;
    private final int unansweredLimit;
    private final int timeToJoinQuiz;
    private final int timeToStartMatch;
    private final int timeToPickAnswer;
    private final int timeForNewQuestionToAppear;

    public TimersConfig(@Value("${mode}") String mode) {
        if ("testing".equals(mode)) {
            this.noOfQuestions = 4;
            this.unansweredLimit = 3;
            this.timeToJoinQuiz = 7;
            this.timeToStartMatch = 2;
            this.timeToPickAnswer = 5;
            this.timeForNewQuestionToAppear = 1;
        } else if ("standard".equals(mode)) {
            this.noOfQuestions = 10;
            this.unansweredLimit = 3;
            this.timeToJoinQuiz = 30;
            this.timeToStartMatch = 3;
            this.timeToPickAnswer = 30;
            this.timeForNewQuestionToAppear = 7;
        } else {
            System.out.println("ERROR! Mode " + mode + " doesn't exist! Check your application.yaml file for mode configuration.");
            this.noOfQuestions = 0;
            this.unansweredLimit = 0;
            this.timeToJoinQuiz = 0;
            this.timeToStartMatch = 0;
            this.timeToPickAnswer = 0;
            this.timeForNewQuestionToAppear = 0;
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