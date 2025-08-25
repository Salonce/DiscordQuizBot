package dev.salonce.discordquizbot.domain;

import java.util.*;
import java.util.stream.Collectors;

public class Scoreboard {
    private final List<PlayerScore> rankedScores;
    private final List<RankGroup> rankGroups;

    public Scoreboard(List<PlayerScore> scores) {
        // Sort by points descending, then by playerId for consistency
        this.rankedScores = scores.stream()
                .sorted(Comparator.comparing(PlayerScore::getPoints).reversed()
                        .thenComparing(PlayerScore::getPlayerId))
                .collect(Collectors.toList());

        this.rankGroups = calculateRankGroups();
    }

    private List<RankGroup> calculateRankGroups() {
        if (rankedScores.isEmpty()) {
            return Collections.emptyList();
        }

        List<RankGroup> groups = new ArrayList<>();
        int currentRank = 1;

        // Group players by points while maintaining rank order
        Map<Integer, List<Long>> pointsToPlayers = new LinkedHashMap<>();

        for (PlayerScore score : rankedScores) {
            pointsToPlayers.computeIfAbsent(score.getPoints(), k -> new ArrayList<>())
                    .add(score.getPlayerId());
        }

        // Convert to RankGroups with proper ranking
        for (Map.Entry<Integer, List<Long>> entry : pointsToPlayers.entrySet()) {
            int points = entry.getKey();
            List<Long> playerIds = entry.getValue();

            groups.add(new RankGroup(currentRank, points, playerIds));
            currentRank += playerIds.size(); // Next rank accounts for ties
        }

        return groups;
    }

    public List<PlayerScore> getRankedScores() {
        return new ArrayList<>(rankedScores);
    }

    public List<RankGroup> getRankGroups() {
        return new ArrayList<>(rankGroups);
    }

    public PlayerScore getWinner() {
        return rankedScores.isEmpty() ? null : rankedScores.get(0);
    }

    public RankGroup getWinningGroup() {
        return rankGroups.isEmpty() ? null : rankGroups.get(0);
    }

    public List<PlayerScore> getPlayersWithScore(int points) {
        return rankedScores.stream()
                .filter(score -> score.getPoints() == points)
                .collect(Collectors.toList());
    }

    public RankGroup getRankGroupWithPoints(int points) {
        return rankGroups.stream()
                .filter(group -> group.getPoints() == points)
                .findFirst()
                .orElse(null);
    }

    public int getRankOf(Long playerId) {
        for (int i = 0; i < rankedScores.size(); i++) {
            if (rankedScores.get(i).getPlayerId().equals(playerId)) {
                return i + 1; // 1-based ranking
            }
        }
        return -1; // Not found
    }

    public RankGroup getRankGroupOf(Long playerId) {
        return rankGroups.stream()
                .filter(group -> group.containsPlayer(playerId))
                .findFirst()
                .orElse(null);
    }
}
