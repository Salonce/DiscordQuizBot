package dev.salonce.discordQuizBot.Core;

import lombok.Getter;

import java.util.List;

//can be a record class
@Getter
public class Question {
    private final String question;
    private final List<Answer> answers;
    private final String explanation;

    public Question(String question, List<Answer> answers, String explanation) {
        this.question = question;
        this.answers = answers;
        this.explanation = explanation;
    }
}
