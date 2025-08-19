package dev.salonce.discordquizbot.domain;

public class Inactivity {
    private int current;
    private final int max;

    public Inactivity(int max) {
        if (max < 1) throw new IllegalArgumentException("Max inactivity must be at least 1");
        this.max = max;
        this.current = 0;
    }

    // Increment current inactivity
    public Inactivity increment() {
        current++;
        return this;
    }

    // Reset current inactivity
    public Inactivity reset() {
        current = 0;
        return this;
    }

    // Check if max inactivity reached
    public boolean isMax() {
        return current >= max;
    }

    public int getCurrent() {
        return current;
    }

    public int getMax() {
        return max;
    }
}