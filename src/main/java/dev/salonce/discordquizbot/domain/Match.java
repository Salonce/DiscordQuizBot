package dev.salonce.discordquizbot.domain;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.NoSuchElementException;

@Getter
public class Match{
    private String topic;
    private final int difficulty;
    private final Map<Long, Player> players = new LinkedHashMap<>();
    private final List<Question> questions;
    private int currentQuestionNum = 0;
    private int inactiveRounds = 0;
    private MatchState matchState = MatchState.ENROLLMENT;

    public Match(List<Question> questions, String topic, int difficulty, Long ownerId){
        this.questions = questions;
        players.put(ownerId, new Player(questions.size()));
        if (topic != null) {
            this.topic = topic.substring(0, 1).toUpperCase() + topic.substring(1);
        }
        this.difficulty = difficulty;
    }

    public void addPlayer(Long userId) {
        if (matchState != MatchState.ENROLLMENT)
            throw new IllegalStateException("Cannot join now.");  // domain-level exception
        if (players.containsKey(userId))
            throw new IllegalStateException("Already joined.");
        players.put(userId, new Player(questions.size()));
    }

    public void closeByOwner(){
        if (!isClosed())
            this.matchState = MatchState.CLOSED_BY_OWNER;
    }

    public void startAnsweringPhase() {
//        if (matchState != MatchState.COUNTDOWN) {
//            throw new IllegalStateException("Cannot close answering if not in countdown phase");
//        }
        this.matchState = MatchState.ANSWERING;
    }

    public void startCountdownPhase(){
        this.matchState = MatchState.COUNTDOWN;
    }

    public void startWaitingPhase() {
        if (matchState != MatchState.ANSWERING) {
            throw new IllegalStateException("Cannot close answering if not in answering phase");
        }
        this.matchState = MatchState.WAITING;
    }

    public boolean isClosed(){
        return ((matchState == MatchState.CLOSED_BY_INACTIVITY) || (matchState == MatchState.CLOSED_BY_OWNER));
    }

    public boolean everyoneAnswered(){
        for (Player player : players.values()){
            if (player.isUnanswered(getCurrentQuestionNum()))
                return false;
        }
        return true;
    }

    public Long getOwnerId(){
        try { return players.keySet().iterator().next(); }
        catch (NoSuchElementException e){ return null; }
    }

    //actually adds +1 point to all players with current question correctly answered, repeating this function in the same round will make results wrong
    public void updateScores(){
        for (Player player : players.values()){
            int playerAnswer = player.getAnswer(currentQuestionNum);
            if (questions.get(currentQuestionNum).isCorrectAnswer(playerAnswer))
                player.addPoint();
        }
    }

    public void skipToNextQuestion(){
        currentQuestionNum++;
    }
    public boolean questionExists(){
        return currentQuestionNum < questions.size();
    }

    public Question getCurrentQuestion(){
        if (currentQuestionNum < questions.size())
            return questions.get(currentQuestionNum);
        else
            return null;
    }

    public void updateInactiveRounds(){
        int noAnswersCount = 0;
        for (Player player : players.values()){
            if (player.isUnanswered(currentQuestionNum))
                noAnswersCount++;
            else break;
        }

        if (noAnswersCount == players.size())
            inactiveRounds++;
        else
            inactiveRounds = 0;
    }

    public void closeIfInactiveLimitReached(int inactiveRoundsLimit){
        if (inactiveRounds >= inactiveRoundsLimit) {
            matchState = MatchState.CLOSED_BY_INACTIVITY;
        }
    }
}
