package dev.salonce.discordquizbot.domain;

public class Inactivity {

    private int current;
    private final int maxAllowed;

    public Inactivity(int maxAllowed) {
        this.maxAllowed = maxAllowed;
        this.current = 0;
    }

    /** Increment the current inactivity count */
    public void increment() {
        current++;
    }

    /** Reset inactivity count to zero */
    public void reset() {
        current = 0;
    }

    /** Get current inactivity count */
    public int getCurrent() {
        return current;
    }

    /** Check if current inactivity exceeds max allowed */
    public boolean exceedsMax() {
        return current > maxAllowed;
    }

    /** Optionally: get max allowed value */
    public int getMaxAllowed() {
        return maxAllowed;
    }
}