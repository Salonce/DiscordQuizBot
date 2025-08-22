package dev.salonce.discordquizbot.application;

import dev.salonce.discordquizbot.domain.Answer;

public final class ResultStatus {
    private final String message;

    private ResultStatus(String message) {
        this.message = message;
    }

    public static ResultStatus alreadyJoined() {
        return new ResultStatus("You've already joined the match!");
    }

    public static ResultStatus matchNotFound() {
        return new ResultStatus("This match doesn't exist anymore!");
    }

    public static ResultStatus notOwner() {
        return new ResultStatus("Only the owner can do this!");
    }

    public static ResultStatus notInMatch() {
        return new ResultStatus("You wanna leave a match you aren't in? Interesting.");
    }

    public static ResultStatus alreadyStarted() {
        return new ResultStatus("This match has already started...");
    }

    public static ResultStatus tooLate() {
        return new ResultStatus("Too late to join");
    }

    public static ResultStatus tooLateToAnswer() {
        return new ResultStatus("Too late to answer this question.");
    }

    public static ResultStatus answerAccepted(Answer answer) {
        return new ResultStatus("Your answer: " + answer.asChar() + ".");
    }

    public static ResultStatus playerJoined() {
        return new ResultStatus("You've joined the match!");
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
        return new ResultStatus("With the undeniable power of ownership, you've aborted the match");
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