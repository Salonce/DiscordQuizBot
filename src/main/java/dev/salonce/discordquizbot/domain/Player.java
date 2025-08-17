package dev.salonce.discordquizbot.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Player {
    private final List<Answer> answersList;

    public Player(int answersSize) {
        this.answersList = new ArrayList<>(Collections.nCopies(answersSize, Answer.none()));
    }

    public Answer getAnswer(int index){
        return answersList.get(index);
    }

    public void setAnswer(int index, Answer answer){
        this.answersList.set(index, answer);
    };

    public boolean isUnanswered(int index){
        return (answersList.get(index).isEmpty());
    }
}