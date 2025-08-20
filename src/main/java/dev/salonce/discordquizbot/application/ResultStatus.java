package dev.salonce.discordquizbot.application;

import dev.salonce.discordquizbot.domain.Answer;

public final class ResultStatus {
    private final String message;

    private ResultStatus(String message) {
        this.message = message;
    }

    public static ResultStatus matchNotFound() {
        return new ResultStatus("Match not found");
    }

    public static ResultStatus notOwner() {
        return new ResultStatus("You are not the owner");
    }

    public static ResultStatus notInMatch() {
        return new ResultStatus("You are not in the match");
    }

    public static ResultStatus alreadyStarted() {
        return new ResultStatus("Match has already started");
    }

    public static ResultStatus tooLate() {
        return new ResultStatus("Too late to join");
    }

    public static ResultStatus tooLateToAnswer() {
        return new ResultStatus("Too late to answer");
    }

    public static ResultStatus answerAccepted(Answer answer) {
        return new ResultStatus("Your answer: " + answer.asChar() + ".");
    }

    // Factory methods for successes
    public static ResultStatus playerJoined() {
        return new ResultStatus("Player joined");
    }

    public static ResultStatus startingImmediately() {
        return new ResultStatus("Starting immediately");
    }

    public static ResultStatus notEnrollment() {
        return new ResultStatus("Excuse me, you can leave the match only during enrollment phase.");
    }

    public static ResultStatus playerLeft() {
        return new ResultStatus("Player left");
    }

    public static ResultStatus matchCancelled() {
        return new ResultStatus("With your undeniable power of ownership, you've cancelled the match");
    }

    public static ResultStatus matchStarted() {
        return new ResultStatus("Match started");
    }

    public static ResultStatus answerRecorded() {
        return new ResultStatus("Answer recorded");
    }

    public static ResultStatus interactionFailed() {
        return new ResultStatus("Button interaction failed");
    }

    // Getter
    public String getMessage() {
        return message;
    }

    // Value-based equality
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResultStatus)) return false;
        ResultStatus that = (ResultStatus) o;
        return message.equals(that.message);
    }

    @Override
    public int hashCode() {
        return message.hashCode();
    }

    @Override
    public String toString() {
        return message;
    }
}