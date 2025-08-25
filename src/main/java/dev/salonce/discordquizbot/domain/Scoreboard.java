package dev.salonce.discordquizbot.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Scoreboard {
    private final List<PlayerScore> rankedScores;

    public Scoreboard(List<PlayerScore> scores) {
        // Sort by points descending, then by playerId for consistency
        this.rankedScores = scores.stream()
                .sorted(Comparator.comparing(PlayerScore::getPoints).reversed()
                        .thenComparing(PlayerScore::getPlayerId))
                .collect(Collectors.toList());
    }

    public List<PlayerScore> getRankedScores() {
        return new ArrayList<>(rankedScores);
    }

    public PlayerScore getWinner() {
        return rankedScores.isEmpty() ? null : rankedScores.get(0);
    }

    public List<PlayerScore> getPlayersWithScore(int points) {
        return rankedScores.stream()
                .filter(score -> score.getPoints() == points)
                .collect(Collectors.toList());
    }

    public int getRankOf(Long playerId) {
        for (int i = 0; i < rankedScores.size(); i++) {
            if (rankedScores.get(i).getPlayerId().equals(playerId)) {
                return i + 1; // 1-based ranking
            }
        }
        return -1; // Not found
    }
}
