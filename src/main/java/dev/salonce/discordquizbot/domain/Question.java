package dev.salonce.discordquizbot.domain;

import lombok.Getter;

import java.util.*;

//can be a record class
@Getter
public class Question {
    private final String question;
    private final String explanation;
    private final List<Option> options;

    public Question(String question, List<Option> options, String explanation) {
        this.question = question;
        this.options = options;
        this.explanation = explanation;
    }

    public boolean isCorrectAnswer(Answer answer){
        if(answer.isEmpty()) return false;
        return (options.get(answer.asNumber()).isCorrect());
    }

    public Answer getCorrectAnswer(){
        for (int i = 0; i < options.size(); i++){
            if (options.get(i).isCorrect())
                return Answer.fromNumber(i);
        }
        return Answer.none();
    }

    public List<Answer> getPossibleAnswers() {
        List<Answer> answers = new ArrayList<>();
        for (int i = 0; i < options.size(); i++) {
            answers.add(Answer.fromNumber(i));
        }
        answers.add(Answer.none());
        return answers;
    }
}
