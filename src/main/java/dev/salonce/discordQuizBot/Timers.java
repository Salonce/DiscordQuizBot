package dev.salonce.discordQuizBot;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public class Timers {

    private final int timeToJoinQuiz; // testing is 7, default is 60
    private final int timeForQuizToStart; // testing is 2, default is 5
    private final int timeToAnswerQuestion; // test is 5, default is 30
    private final int timeForNewQuestionToAppear; //test is 3, default is 8

    @PostConstruct
    public void timers() {
        System.out.println("Set timers:");
        System.out.println("timeToJoinQuiz: " + timeToJoinQuiz + "\ntimeForQuizToStart: " + timeForQuizToStart + "\ntimeToAnswerQuestion: " + timeToAnswerQuestion + "\ntimeForNewQuestionToAppear: " + timeForNewQuestionToAppear);
    }
}
