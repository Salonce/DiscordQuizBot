package dev.salonce.discordQuizBot.Core.Questions;

import lombok.Getter;

import java.util.List;
import java.util.stream.IntStream;

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

//    public List<Integer> getCorrectAnswerListInt(){
//        return IntStream.range(0, answers.size())
//                .filter(i -> answers.get(i).correctness())
//                .boxed()
//                .toList();
//    }

    public String getCorrectAnswerString(){
        int corAns = getCorrectAnswerInt();
        if (corAns != -1)
            return answers.get(corAns).answer();
        return "No correct answer";
    }

    public int getCorrectAnswerInt(){
        for (int i = 0; i < answers.size(); i++){
            if (answers.get(i).correctness())
                return i;
        }
        return -1;
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
