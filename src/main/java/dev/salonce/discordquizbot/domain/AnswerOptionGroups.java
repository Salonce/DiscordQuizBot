package dev.salonce.discordquizbot.domain;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AnswerOptionGroups {

    private final List<AnswerOptionGroup> answerOptionGroups;
    private final AnswerOptionGroup noAnswerGroup;

    public AnswerOptionGroups(List<AnswerOptionGroup> answerOptionGroups,
                              AnswerOptionGroup noAnswerGroup) {
        this.answerOptionGroups = List.copyOf(
                Objects.requireNonNull(answerOptionGroups, "answerOptionGroups cannot be null"));
        this.noAnswerGroup = Objects.requireNonNull(noAnswerGroup, "noAnswerGroup cannot be null");
    }

    public List<AnswerOptionGroup> getAnswerOptionGroups() {
        return Collections.unmodifiableList(answerOptionGroups);
    }

    public AnswerOptionGroup getNoAnswerGroup() {
        return noAnswerGroup;
    }

    public int getTotalUserCount() {
        return answerOptionGroups.stream()
                .mapToInt(AnswerOptionGroup::size)
                .sum() + noAnswerGroup.size();
    }

    public boolean isEmpty() {
        return getTotalUserCount() == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnswerOptionGroups that)) return false;
        return Objects.equals(answerOptionGroups, that.answerOptionGroups) &&
                Objects.equals(noAnswerGroup, that.noAnswerGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(answerOptionGroups, noAnswerGroup);
    }

    @Override
    public String toString() {
        return "AnswerDistribution{" +
                "answerOptionGroups=" + answerOptionGroups +
                ", noAnswerGroup=" + noAnswerGroup +
                '}';
    }
}