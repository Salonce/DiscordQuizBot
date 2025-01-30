package dev.salonce.discordQuizBot.Core.Matches;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class Player {

    private List<Integer> answersList;

    public Player(int numOfAnswers){
        this.points = 0;
        answersList = new ArrayList<>();
        for (int i = 0; i < numOfAnswers; i++){
            answersList.add(-1);
        }
        //this.answersList = new ArrayList<>(Collections.nCopies(numOfAnswers, -1));
    }
    private int points;

    public void addPoint(){
        this.points++;
    }
}