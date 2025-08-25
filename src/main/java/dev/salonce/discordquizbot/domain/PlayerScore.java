package dev.salonce.discordquizbot.domain;

import java.util.Objects;

public class PlayerScore {
    private final Long playerId;
    private final int points;

    public PlayerScore(Long playerId, int points) {
        this.playerId = playerId;
        this.points = points;
    }

    public Long getPlayerId() { return playerId; }
    public int getPoints() { return points; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerScore)) return false;
        PlayerScore that = (PlayerScore) o;
        return points == that.points && Objects.equals(playerId, that.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, points);
    }
}
