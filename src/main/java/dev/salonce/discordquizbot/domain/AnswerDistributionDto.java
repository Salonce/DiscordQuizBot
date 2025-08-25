package dev.salonce.discordquizbot.domain;

import java.util.List;
import java.util.Objects;


public final class AnswerDistributionDto {
    private final List<AnswerOptionGroup> answerGroups;
    private final AnswerOptionGroup noAnswerGroup;
    private final Answer correctAnswer;
    private final int totalOptions;

    public AnswerDistributionDto(List<AnswerOptionGroup> answerGroups,
                              AnswerOptionGroup noAnswerGroup,
                              Answer correctAnswer,
                              int totalOptions) {
        this.answerGroups = List.copyOf(answerGroups);
        this.noAnswerGroup = Objects.requireNonNull(noAnswerGroup);
        this.correctAnswer = Objects.requireNonNull(correctAnswer);
        this.totalOptions = totalOptions;
    }

    public boolean isCorrectAnswer(Answer answer) {
        return correctAnswer.equals(answer);
    }

    public List<AnswerOptionGroup> getAnswerGroups() { return answerGroups; }
    public AnswerOptionGroup getNoAnswerGroup() { return noAnswerGroup; }
    public Answer getCorrectAnswer() { return correctAnswer; }
    public int getTotalOptions() { return totalOptions; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnswerDistributionDto)) return false;
        AnswerDistributionDto that = (AnswerDistributionDto) o;
        return totalOptions == that.totalOptions &&
                answerGroups.equals(that.answerGroups) &&
                noAnswerGroup.equals(that.noAnswerGroup) &&
                correctAnswer.equals(that.correctAnswer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(answerGroups, noAnswerGroup, correctAnswer, totalOptions);
    }

    @Override
    public String toString() {
        return "AnswerDistribution{" +
                "answerGroups=" + answerGroups +
                ", noAnswerGroup=" + noAnswerGroup +
                ", correctAnswer=" + correctAnswer +
                ", totalOptions=" + totalOptions +
                '}';
    }
}