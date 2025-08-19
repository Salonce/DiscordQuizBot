package dev.salonce.discordquizbot.domain;

import java.util.Objects;

public final class AnswerEntry {

    private final int questionIndex;
    private final Answer answer;

    private AnswerEntry(int questionIndex, Answer answer) {
        if (questionIndex < 0) {
            throw new IllegalArgumentException("Question index cannot be negative");
        }
        this.questionIndex = questionIndex;
        this.answer = Objects.requireNonNull(answer, "Answer cannot be null");
    }

    public static AnswerEntry of(int questionIndex, Answer answer) {
        return new AnswerEntry(questionIndex, answer);
    }

    public int getQuestionIndex() {
        return questionIndex;
    }

    public Answer getAnswer() {
        return answer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnswerEntry)) return false;
        AnswerEntry that = (AnswerEntry) o;
        return questionIndex == that.questionIndex && answer.equals(that.answer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionIndex, answer);
    }

    @Override
    public String toString() {
        return "AnswerEntry{" +
                "questionIndex=" + questionIndex +
                ", answer=" + answer +
                '}';
    }
}
