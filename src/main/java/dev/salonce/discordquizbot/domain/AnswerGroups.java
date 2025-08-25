package dev.salonce.discordquizbot.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnswerGroups {

    List<AnswerGroup> answerGroups = new ArrayList<>();

    public AnswerGroups(Question question, Players players){


    }

    public Map<Answer, List<Long>> getPlayersGroupedByAnswer(int index) {
        Map<Answer, List<Long>> groups = new HashMap<>();

        players.forEach((playerId, player) -> {
            Answer answer = player.getAnswer(index);
            groups.computeIfAbsent(answer, k -> new ArrayList<>()).add(playerId);
        });

        return groups;
    }
}
