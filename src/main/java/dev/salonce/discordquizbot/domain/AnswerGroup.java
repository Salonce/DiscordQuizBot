package dev.salonce.discordquizbot.domain;

import java.util.*;

import java.util.Collections;
import java.util.List;

public class AnswerGroup {

    private final List<Long> userIds;

    public AnswerGroup(List<Long> userIds) {
        this.userIds = List.copyOf(userIds);
    }

//    public List<Long> getAnswerGroup(Players players, int questionIndex, Answer answer) {
//        List<Long> group = new ArrayList<>();
//
//        players.getPlayersMap().forEach((playerId, player) -> {
//            if (player.getAnswer(questionIndex).equals(answer))
//                group.add(playerId);
//        });
//        return group;
//    }

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

    public AnswerGroup addUser(Long userId) {
        List<Long> newList = new java.util.ArrayList<>(userIds);
        newList.add(userId);
        return new AnswerGroup(newList);
    }

    public AnswerGroup removeUser(Long userId) {
        List<Long> newList = new java.util.ArrayList<>(userIds);
        newList.remove(userId);
        return new AnswerGroup(newList);
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