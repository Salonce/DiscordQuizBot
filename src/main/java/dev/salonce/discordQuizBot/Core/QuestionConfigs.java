package dev.salonce.discordQuizBot.Core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
public class QuestionConfigs {

    @Bean("javaQuestions")
    public List<Question> javaQuestions() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File("src/main/resources/java.json");

        Question[] questions = mapper.readValue(file, Question[].class);

//        for (Question question : questions) {
//            System.out.println(question.getQuestion());
//            List<Answer> answers = question.getAnswers();
//
//            for (Answer answer : answers) {
//                System.out.println(answer.answer() + ", " + answer.correctness());
//            }
//            System.out.println();
//        }

        return Arrays.asList(questions);
    }
}
