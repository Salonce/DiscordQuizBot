package dev.salonce.discordQuizBot.Core;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class Question {

    private String question;
    private List<String> correctAnswers;
    private List<String> incorrectAnswers;
    private String explanation;


    public List<Answer> getAnswers(){

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

        return answers;
    }
}

