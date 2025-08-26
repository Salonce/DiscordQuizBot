package dev.salonce.discordquizbot.domain;

public class Inactivity {
    private int current;
    private final int maxAllowed;

    public Inactivity(int maxAllowed) {
        this.maxAllowed = maxAllowed;
        this.current = 0;
    }

    public void increment() {
        current++;
    }

    public void reset() {
        current = 0;
    }

    public int getCurrent() {
        return current;
    }

    public boolean exceedsMax() {
        return current > maxAllowed;
    }

    public int getMaxAllowed() {
        return maxAllowed;
    }
}