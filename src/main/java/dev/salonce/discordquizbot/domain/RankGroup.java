package dev.salonce.discordquizbot.domain;

import java.util.ArrayList;
import java.util.List;

public class RankGroup {
    private final int rank;
    private final int points;
    private final List<Long> playerIds;

    public RankGroup(int rank, int points, List<Long> playerIds) {
        this.rank = rank;
        this.points = points;
        this.playerIds = new ArrayList<>(playerIds);
    }

    public int getRank() { return rank; }
    public int getPoints() { return points; }
    public List<Long> getPlayerIds() { return new ArrayList<>(playerIds); }
    public int getPlayerCount() { return playerIds.size(); }

    public boolean isWinningRank() { return rank == 1; }
    public boolean containsPlayer(Long playerId) { return playerIds.contains(playerId); }
}
