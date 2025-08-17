package dev.salonce.discordquizbot.domain;

import lombok.Getter;

import java.util.*;

//can be a record class
@Getter
public class Question {
    private final String question;
    private final String explanation;
    private final List<QuestionOption> questionOptions;

    public Question(String question, List<QuestionOption> questionOptions, String explanation) {
        this.question = question;
        this.questionOptions = questionOptions;
        this.explanation = explanation;
    }

    public boolean isCorrectAnswer(Answer answer){
        if(answer.isEmpty()) return false;
        return (questionOptions.get(answer.asNumber()).isCorrect());
    }

    public Answer getCorrectAnswer(){
        for (int i = 0; i < questionOptions.size(); i++){
            if (questionOptions.get(i).isCorrect())
                return Answer.fromNumber(i);
        }
        return Answer.none();
    }
}
