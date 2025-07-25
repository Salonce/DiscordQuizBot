package dev.salonce.discordquizbot.core.questions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.*;

@Getter
public class RawQuestion {

    private final Long id;
    private final String question;
    private final List<String> correctAnswers;
    private final List<String> incorrectAnswers;
    private final String explanation;
    private final Integer difficulty;
    private final Set<String> tags;

    public RawQuestion(@JsonProperty("id") Long id, @JsonProperty("question") String question, @JsonProperty("correctAnswers") List<String> correctAnswers, @JsonProperty("incorrectAnswers") List<String> incorrectAnswers, @JsonProperty("explanation") String explanation, @JsonProperty("difficulty") Integer difficulty, @JsonProperty("tags") Set<String> tags) {

        this.id = id;
        this.question = question;
        this.correctAnswers = correctAnswers;
        this.incorrectAnswers = incorrectAnswers;
        this.explanation = explanation;
        this.difficulty = difficulty;
        this.tags = tags;


    }

    public boolean containsTag(String tag){
        if (tags == null) {
            System.out.println("Missing or null tags for question ID: " + id);
            System.out.println("question: " + question);
        }
        return tags.contains(tag);
    }


//    public Question generateQuestion(){
//
//        //make an text list
//        List<QuizOption> quizOptions = new ArrayList<>();
//
//        //add correct answers
//        Random rand = new Random();
//        int num = rand.nextInt(correctAnswers.size());
//        quizOptions.add(new QuizOption(correctAnswers.get(num), true));
//
//        //add incorrect answers
//        Set<Integer> set = new HashSet();
//        int size = Math.min(3, incorrectAnswers.size());
//        while (set.size() != size){
//            num = rand.nextInt(incorrectAnswers.size());
//            set.add(num);
//        }
//
//        for (int i : set){
//            quizOptions.add(new QuizOption(incorrectAnswers.get(i), false));
//        }
//
//        //shuffle the list
//        Collections.shuffle(quizOptions);
//
//        return new Question(question, quizOptions, explanation);
//    }

}

