package dev.salonce.discordQuizBot.Core.Matches;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class Player {
    public Player(int numOfAnswers){
        this.points = 0;
        this.answersList = new ArrayList<>(Collections.nCopies(numOfAnswers, -1));
    }
    private int points;

    private List<Integer> answersList;

    @Setter
    private Character currentAnswer = '0';

    @Setter
    private Integer currentAnswerNum = -1;

    public void addPoint(){
        this.points++;
    }
}