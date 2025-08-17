package dev.salonce.discordquizbot.domain;

import java.util.Objects;

public final class Answer {
    private final Integer index; // 0–3, null means "no answer"
    private static final char[] LETTERS = {'A', 'B', 'C', 'D'};
    private Answer(Integer index) {
        if (index != null && (index < 0 || index > 3)) {
            throw new IllegalArgumentException("Answer index must be between 0 and 3 or null");
        }
        this.index = index;
    }
    public static Answer fromNumber(int index) {
        return new Answer(index);
    }
    public static Answer fromChar(char letter) {
        letter = Character.toUpperCase(letter);
        for (int i = 0; i < LETTERS.length; i++) {
            if (LETTERS[i] == letter) {
                return new Answer(i);
            }
        }
        throw new IllegalArgumentException("Invalid answer letter: " + letter);
    }
    public static Answer none() {
        return new Answer(null);
    }
    public boolean isEmpty() {
        return index == null;
    }
    public Integer asNumber() {
        return index;
    }
    public Character asChar() {
        return index == null ? null : LETTERS[index];
    }
    public boolean isCorrect(Answer correct) {
        return Objects.equals(this.index, correct.index);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Answer)) return false;
        Answer other = (Answer) o;
        return Objects.equals(index, other.index);
    }
    @Override
    public int hashCode() {
        return Objects.hash(index);
    }
    @Override
    public String toString() {
        return isEmpty() ? "—" : asChar().toString();
    }
}