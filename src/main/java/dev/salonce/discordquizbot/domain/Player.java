package dev.salonce.discordquizbot.domain;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private final List<Integer> answersList;

    public void setAnswer(int questionNumber, int answerNumber){
        this.answersList.set(questionNumber, answerNumber);
    };

    public boolean isUnanswered(int index){
        return (answersList.get(index) == -1);
    }

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