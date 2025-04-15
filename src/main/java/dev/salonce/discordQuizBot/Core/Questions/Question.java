package dev.salonce.discordQuizBot.Core.Questions;

import lombok.Getter;

import java.util.*;

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

    public Question (RawQuestion rawQuestion){
        List<QuizOption> quizOptions = new ArrayList<>();

        Random rand = new Random();
        int num = rand.nextInt(rawQuestion.getCorrectAnswers().size());
        quizOptions.add(new QuizOption(rawQuestion.getCorrectAnswers().get(num), true));

        //add incorrect answers
        Set<Integer> set = new HashSet();
        int size = Math.min(3, rawQuestion.getIncorrectAnswers().size());
        while (set.size() != size){
            num = rand.nextInt(rawQuestion.getIncorrectAnswers().size());
            set.add(num);
        }

        for (int i : set){
            quizOptions.add(new QuizOption(rawQuestion.getIncorrectAnswers().get(i), false));
        }

        Collections.shuffle(quizOptions);

        this.question = rawQuestion.getQuestion();
        this.explanation = rawQuestion.getExplanation();
        this.quizOptions = quizOptions;
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
