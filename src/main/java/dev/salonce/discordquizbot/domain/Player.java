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

    public List<Answer> getAnswersList() {
        return answersList;
    }

    public long calculateScore(List<Answer> correctAnswers) {
        long score = 0;
        for (int i = 0; i < correctAnswers.size(); i++) {
            if (answersList.get(i).equals(correctAnswers.get(i))) {
                score++;
            }
        }
        return score;
    }


}