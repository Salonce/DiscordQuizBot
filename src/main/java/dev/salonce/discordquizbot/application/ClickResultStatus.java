package dev.salonce.discordquizbot.application;

public enum ClickResultStatus {
    MATCH_NOT_FOUND,
    NOT_OWNER,
    NOT_IN_MATCH,
    ALREADY_STARTED,
    TOO_LATE,

    // Successes
    PLAYER_JOINED,
    PLAYER_LEFT,
    MATCH_CANCELLED,
    MATCH_STARTED,
    ANSWER_RECORDED
}
