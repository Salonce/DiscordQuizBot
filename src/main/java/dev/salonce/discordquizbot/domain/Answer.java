package dev.salonce.discordquizbot.domain;

import java.util.OptionalInt;

public class Answer {
    private final OptionalInt value;
    public Answer(int value) { this.value = OptionalInt.of(value); }
    public Answer() { this.value = OptionalInt.empty(); }
    public boolean isAnswered() { return value.isPresent(); }
    public int getValue() { return value.orElseThrow(); }
}
