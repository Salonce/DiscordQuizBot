package dev.salonce.discordquizbot.domain;

import java.util.List;
import java.util.Objects;

public class AnswerDistributionDto {
    private final List<AnswerOptionGroup> answerGroups;
    private final AnswerOptionGroup noAnswerGroup;
    private final Answer correctAnswer;  // optional: part of domain rules
    private final int totalOptions;      // optional: metadata

    public AnswerDistributionDto(List<AnswerOptionGroup> answerGroups,
                                  AnswerOptionGroup noAnswerGroup,
                                  Answer correctAnswer,
                                  int totalOptions) {
        this.answerGroups = List.copyOf(answerGroups);
        this.noAnswerGroup = Objects.requireNonNull(noAnswerGroup);
        this.correctAnswer = correctAnswer;
        this.totalOptions = totalOptions;
    }

    public boolean isCorrectAnswer(Answer answer){
        return answer.equals(correctAnswer);
    }

    // getters only
    public List<AnswerOptionGroup> getAnswerGroups() { return answerGroups; }
    public AnswerOptionGroup getNoAnswerGroup() { return noAnswerGroup; }
    public Answer getCorrectAnswer() { return correctAnswer; }
    public int getTotalOptions() { return totalOptions; }
}