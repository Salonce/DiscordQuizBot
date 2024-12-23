package dev.salonce.discordQuizBot.Configs;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public class Timers {

    private final int timeToJoinQuiz;
    private final int timeToStartMatch;
    private final int timeToPickAnswer;
    private final int timeForNewQuestionToAppear;

    @PostConstruct
    public void timers() {
        System.out.println("Set timers:");
        System.out.println("timeToJoinQuiz: " + timeToJoinQuiz + "\ntimeForQuizToStart: " + timeToStartMatch + "\ntimeToAnswerQuestion: " + timeToPickAnswer + "\ntimeForNewQuestionToAppear: " + timeForNewQuestionToAppear);
    }
}
