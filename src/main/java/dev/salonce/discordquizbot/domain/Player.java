package dev.salonce.discordquizbot.domain;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private final List<Integer> answersList;

    @Getter
    private int points;

    public void setAnswer(int index, int answer){
        this.answersList.set(index, answer);
    };

    public void setAnswerAsLetter(int index, char letter) {
        int answerNumber = letter - 'A';  // Convert 'A', 'B', 'C', etc. to 0, 1, 2...
        answersList.set(index, answerNumber);
    }

    public String getAnswerAsLetter(int index) {
        int answer = answersList.get(index);
        return answer >= 0 ? String.valueOf((char) ('A' + answer)) : "?";
    }

    public boolean isUnanswered(int index){
        return (answersList.get(index) == -1);
    }

    public int getAnswer(int index){
        return answersList.get(index);
    }

    public Player(int numOfAnswers){
        this.points = 0;
        answersList = new ArrayList<>();
        for (int i = 0; i < numOfAnswers; i++){
            answersList.add(-1);
        }
        //this.answersList = new ArrayList<>(Collections.nCopies(numOfAnswers, -1));
    }

    public void addPoint(){
        this.points++;
    }
}