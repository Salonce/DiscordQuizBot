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

//    public List<Integer> getCorrectAnswerListInt(){
//        return IntStream.range(0, answers.size())
//                .filter(i -> answers.get(i).correctness())
//                .boxed()
//                .toList();
//    }

    public String getCorrectAnswerString(){
        int corAns = getCorrectAnswerInt();
        if (corAns != -1)
            return answers.get(corAns).text();
        return "No correct text";
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

    public String getStringAnswers(boolean showAnswers){
        StringBuilder sb = new StringBuilder();
        char letter = 'A';
        for (Answer answer : answers){
            if (showAnswers && answer.correctness()) sb.append("**");
            //if (showAnswers && !answer.correctness()) sb.append("~~");
            sb.append(letter + ") " + answer.text());
            letter++;
            //if (showAnswers && !answer.correctness()) sb.append("~~");
           // if (showAnswers && !answer.correctness()) sb.append(" ✗ ❌");
            //if (showAnswers && answer.correctness()) sb.append(" ✔ ✅**");
            if (showAnswers && !answer.correctness()) sb.append(" ❌");
            if (showAnswers && answer.correctness()) sb.append("** ✅");
            sb.append("\n");
        }
        return sb.toString();
    }
}
