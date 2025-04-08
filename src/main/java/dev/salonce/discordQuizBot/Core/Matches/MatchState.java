package dev.salonce.discordQuizBot.Core.Matches;

public enum MatchState {
    ENROLLMENT,
    COUNTDOWN,
    ANSWERING,
    WAITING,
    CLOSED_BY_OWNER,
    CLOSED_BY_INACTIVITY
}
