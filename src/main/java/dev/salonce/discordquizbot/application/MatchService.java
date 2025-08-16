package dev.salonce.discordquizbot.application;

import dev.salonce.discordquizbot.domain.Match;
import dev.salonce.discordquizbot.domain.MatchState;
import dev.salonce.discordquizbot.domain.Question;
import dev.salonce.discordquizbot.infrastructure.MatchCache;
import dev.salonce.discordquizbot.infrastructure.configs.TimersConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class MatchService {

    private final MatchCache matchCache;
    private final TimersConfig timersConfig;
    private final QuestionsService questionsService;

    public Match makeMatch(String topic, int difficulty, Long ownerId){
        List<Question> questions = questionsService.generateQuestions(topic, difficulty, timersConfig.getNoOfQuestions());
        return new Match(questions, topic, difficulty, ownerId, timersConfig.getUnansweredLimit());
    }

    public Match get(Long channelId) {
        return matchCache.get(channelId);
    }

    public void put(Long channelId, Match match) {
        matchCache.put(channelId, match);
    }

    public boolean containsKey(Long channelId) {
        return matchCache.containsKey(channelId);
    }

    public void remove(Long channelId) {
        matchCache.remove(channelId);
    }

    public Collection<Match> getAll() {
        return matchCache.getAll();
    }

    public String addPlayerToMatch(Long channelId, Long userId) {
        Match match = matchCache.get(channelId);
        if (match == null) return "This match doesn't exist.";
        try { match.addPlayer(userId); }
        catch (IllegalStateException e) { return e.getMessage(); }
        matchCache.put(channelId, match);
        return "You've joined the match.";
    }

    public String cancelMatch (Long channelId, Long userId) {
        Match match = get(channelId);
        if (match == null)
            return "This match doesn't exist anymore.";
        if (!match.getOwnerId().equals(userId))
            return "You are not the owner. Only the owner can cancel the match.";
        match.setMatchState(MatchState.CLOSED_BY_OWNER);
        return "With your undeniable power of ownership, you've cancelled the match";
    }

    public String leaveMatch(Long channelId, Long userId) {
        Match match = get(channelId);
        if (match == null) {
            return "This match doesn't exist.";
        }
        if (!match.getPlayers().containsKey(userId)) {
            return "You are not even in the match.";
        }
        if (match.getMatchState() != MatchState.ENROLLMENT) {
            return "Excuse me, you can leave the match only during enrollment phase.";
        } else {
            match.getPlayers().remove(userId);
            return "You've left the match.";
        }
    }

    public String startNow(Long channelId, Long userId) {
        if (!containsKey(channelId))
            return "This match doesn't exist anymore.";
        if (!Objects.equals(userId, get(channelId).getOwnerId()))
            return "You aren't the owner";
        if (get(channelId).getMatchState() != MatchState.ENROLLMENT)
            return "Already started";

        get(channelId).setMatchState(MatchState.COUNTDOWN);
        return "Starting immediately";
    }

    public String getPlayerAnswer(Long channelId, Long userId, int questionNumber, int answerNumber) {
        Match match = get(channelId);

        if (match == null || questionNumber != match.getCurrentQuestionNum() || match.getMatchState() != MatchState.ANSWERING)
            return "Your answer came too late!";

        if (!match.getPlayers().containsKey(userId))
            return "You are not in the match.";

        List<Integer> answers = match.getPlayers().get(userId).getAnswersList();
        answers.set(questionNumber, answerNumber);
        return "Your answer: " + (char) ('A' + answerNumber) + ".";

    }
}
