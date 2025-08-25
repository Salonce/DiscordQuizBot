package dev.salonce.discordquizbot.domain;

import java.util.*;

import java.util.Collections;
import java.util.List;

public class AnswerGroup {

    private final List<Long> userIds;

    public AnswerGroup(List<Long> userIds) {
        this.userIds = List.copyOf(userIds);
    }

    public List<Long> getUserIds() {
        return Collections.unmodifiableList(userIds);
    }

    public int size() {
        return userIds.size();
    }

    public boolean isEmpty() {
        return userIds.isEmpty();
    }

    public boolean contains(Long userId) {
        return userIds.contains(userId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnswerGroup that)) return false;
        return Objects.equals(userIds, that.userIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userIds);
    }

    @Override
    public String toString() {
        return "AnswerGroup{" + "userIds=" + userIds + '}';
    }
}