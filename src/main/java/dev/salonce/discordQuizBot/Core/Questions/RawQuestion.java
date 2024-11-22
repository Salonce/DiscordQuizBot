package dev.salonce.discordQuizBot.Core.Questions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.*;

@Getter
public class RawQuestion {

    private final String question;
    private final List<String> correctAnswers;
    private final List<String> incorrectAnswers;
    private final String explanation;

    public RawQuestion(@JsonProperty("question") String question, @JsonProperty("correctAnswers") List<String> correctAnswers, @JsonProperty("incorrectAnswers") List<String> incorrectAnswers, @JsonProperty("explanation") String explanation) {
        this.question = question;
        this.correctAnswers = correctAnswers;
        this.incorrectAnswers = incorrectAnswers;
        this.explanation = explanation;
    }

    public Question generateQuestion(){

        //make an answer list
        List<Answer> answers = new ArrayList<>();

        //add correct answers
        Random rand = new Random();
        int num = rand.nextInt(correctAnswers.size());
        answers.add(new Answer(correctAnswers.get(num), true));

        //add incorrect answers
        Set<Integer> set = new HashSet();
        int size = Math.min(3, incorrectAnswers.size());
        while (set.size() != size){
            num = rand.nextInt(incorrectAnswers.size());
            set.add(num);
        }

        for (int i : set){
            answers.add(new Answer(incorrectAnswers.get(i), false));
        }

        //shuffle the list
        Collections.shuffle(answers);

        return new Question(question, answers, explanation);
    }

}

