package dev.salonce.discordQuizBot.Core.Questions;

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

    public Character getCorrectAnswer(){
        for (int i = 0; i < answers.size(); i++){
            if (answers.get(i).correctness())
                return (char)('A' + i);
        }
        return null;
    }

    public String getStringAnswers(){
        StringBuilder sb = new StringBuilder();
        char letter = 'A';
        for (Answer answer : answers){
            sb.append(letter + ") " + answer.answer() + "\n");
            letter++;
        }
        return sb.toString();
    }
}
