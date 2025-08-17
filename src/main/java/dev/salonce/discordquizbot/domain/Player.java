package dev.salonce.discordquizbot.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Player {
    private final List<Integer> answersList;

    public Player(int numOfQuestions) {
        this.answersList = new ArrayList<>(Collections.nCopies(numOfQuestions, -1));
    }

    public int getAnswer(int index){
        return answersList.get(index);
    }

    public void setAnswer(int index, int answer){
        this.answersList.set(index, answer);
    };

    public boolean isUnanswered(int index){
        return (answersList.get(index) == -1);
    }
}