package dev.salonce.discordquizbot.domain;

import lombok.Getter;

import java.util.*;

//can be a record class
@Getter
public class Question {
    private final String question;
    private final String explanation;
    private final List<QuizOption> quizOptions;

    public Question(String question, List<QuizOption> quizOptions, String explanation) {
        this.question = question;
        this.quizOptions = quizOptions;
        this.explanation = explanation;
    }

    public boolean isCorrectAnswer(Answer answer){
        if(answer.isEmpty()) return false;
        return (quizOptions.get(answer.asNumber()).isCorrect());
    }

    public Answer getCorrectAnswer(){
        for (int i = 0; i < quizOptions.size(); i++){
            if (quizOptions.get(i).isCorrect())
                return Answer.fromNumber(i);
        }
        return Answer.none();
    }
}
