package dev.salonce;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File("src/main/resources/java.json");

        Question[] questions = mapper.readValue(file, Question[].class);


        for (Question question : questions){
            System.out.println(question.getQuestion());
            List<Answer> answers = question.getAnswers();

            for (Answer answer : answers) {
                System.out.println(answer.answer() + ", " + answer.correctness());
            }
            System.out.println();
        }
    }
}