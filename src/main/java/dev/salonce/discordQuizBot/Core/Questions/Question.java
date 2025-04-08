package dev.salonce.discordQuizBot.Core.Questions;

import lombok.Getter;

import java.util.List;

//can be a record class
@Getter
public class Question {
    private final String question;
    private final List<QuizOption> quizOptions;
    private final String explanation;

    public Question(String question, List<QuizOption> quizOptions, String explanation) {
        this.question = question;
        this.quizOptions = quizOptions;
        this.explanation = explanation;
    }

    public int getCorrectAnswerInt(){
        for (int i = 0; i < quizOptions.size(); i++){
            if (quizOptions.get(i).isCorrect())
                return i;
        }
        return -1;
    }

    public String getOptions(){
        StringBuilder sb = new StringBuilder();
        char letter = 'A';
        for (QuizOption quizOption : quizOptions){
            sb.append(letter).append(") ").append(quizOption.text());
            letter++;
            sb.append("\n");
        }
        return sb.toString();
    }

    public String getOptionsRevealed(){
        StringBuilder sb = new StringBuilder();
        char letter = 'A';
        for (QuizOption quizOption : quizOptions){
            if (!quizOption.isCorrect()) sb.append("❌ ").append(letter).append(") ").append(quizOption.text());
            if (quizOption.isCorrect()) sb.append("✅** ").append(letter).append(") ").append(quizOption.text()).append("**");
            letter++;
            sb.append("\n");
        }
        return sb.toString();
    }

    public Character getCorrectAnswer(){
        for (int i = 0; i < quizOptions.size(); i++){
            if (quizOptions.get(i).isCorrect())
                return (char)('A' + i);
        }
        return null;
    }

    public String getCorrectAnswerString(){
        int corAns = getCorrectAnswerInt();
        if (corAns != -1)
            return quizOptions.get(corAns).text();
        return "No correct text";
    }
}
