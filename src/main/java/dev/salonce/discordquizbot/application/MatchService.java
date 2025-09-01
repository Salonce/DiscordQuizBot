package dev.salonce.discordquizbot.application;

import dev.salonce.discordquizbot.domain.*;
import dev.salonce.discordquizbot.domain.exceptions.NotEnrollmentState;
import dev.salonce.discordquizbot.domain.exceptions.UserAlreadyJoined;
import dev.salonce.discordquizbot.infrastructure.configs.QuizSetupConfig;
import dev.salonce.discordquizbot.infrastructure.storage.MatchCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class MatchService {

    private final MatchCache matchCache;
    private final QuestionsService questionsService;
    private final QuizSetupConfig config;

    public Match makeMatch(String topic, int difficulty, Long ownerId){
        Questions questions = questionsService.generateQuestions(topic, difficulty);
        String title  = topic.substring(0, 1).toUpperCase() + topic.substring(1);
        Inactivity inactivity = new Inactivity(config.getMaxInactivityCount());
        return new Match(questions, title, difficulty, ownerId, inactivity);
    }

    private Match get(Long channelId) {
        return matchCache.get(channelId);
    }

    public void put(Long channelId, Match match) {
        matchCache.put(channelId, match);
    }

    public boolean matchExists(Long channelId) {
        return matchCache.containsKey(channelId);
    }

    public void remove(Long channelId) {
        matchCache.remove(channelId);
    }

    public ResultStatus addPlayerToMatch(Long channelId, Long userId) {
        Match match = matchCache.get(channelId);
        if (match == null) return ResultStatus.matchNotFound();
        try { match.addPlayer(userId); }
        catch (NotEnrollmentState e) { return ResultStatus.tooLate(); }
        catch (UserAlreadyJoined e) { return ResultStatus.alreadyJoined(); }
        matchCache.put(channelId, match);
        return ResultStatus.playerJoined();
    }

    public ResultStatus ownerCancelsMatch(Long channelId, Long userId) {
        Match match = get(channelId);
        if (match == null)
            return ResultStatus.matchNotFound();
        if (!match.isOwner(userId))
            return ResultStatus.notOwner();
        match.abortByOwner();
        return ResultStatus.matchCancelled();
    }

    public ResultStatus removeUserFromMatch(Long channelId, Long userId) {
        Match match = get(channelId);
        if (match == null)
            return ResultStatus.matchNotFound();
        if (!match.playerExists(userId))
            return ResultStatus.notInMatch();
        if (!match.isEnrolling()) {
            return ResultStatus.notEnrollment();
        }
        match.removeUser(userId);
        return ResultStatus.playerLeft();
    }

    public ResultStatus ownerStartsMatch(Long channelId, Long userId) {
        if (!matchExists(channelId))
            return ResultStatus.matchNotFound();
        if (!Objects.equals(userId, get(channelId).getOwnerId()))
            return ResultStatus.notOwner();
        if (!get(channelId).isEnrolling())
            return ResultStatus.alreadyStarted();

        get(channelId).startCountdownPhase();
        return ResultStatus.startingImmediately();
    }

    public ResultStatus addPlayerAnswer(Long channelId, Long userId, int questionIndex, Answer answer) {
        Match match = get(channelId);

        if (match == null || !match.isCurrentQuestion(questionIndex) || !match.isAnsweringState())
            return ResultStatus.tooLateToAnswer();

        if (!match.playerExists(userId))
            return ResultStatus.notInMatch();

        match.setPlayerAnswer(userId, questionIndex, answer);
        return ResultStatus.answerAccepted(answer);
    }
}
