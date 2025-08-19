package dev.salonce.discordquizbot.domain;

public enum MatchState {
    ENROLLMENT,
    COUNTDOWN,
    QUESTION,
    BETWEEN_QUESTIONS,
    ABORTED_BY_OWNER,
    ABORTED_BY_INACTIVITY,
    FINISHED
}
