package dev.salonce.discordQuizBot.Core.Matches;

public enum MatchState {
    ENROLLMENT,
    COUNTDOWN,
    QUIZ_ANSWERING,
    QUIZ_WAITING,
    CLOSED_BY_OWNER,
    CLOSED_BY_INACTIVITY
}
