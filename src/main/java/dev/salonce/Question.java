package dev.salonce;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Question {
    private String question;
    private List<String> correctAnswers;
    private List<String> wrongAnswers;
    private String explanation;
}
