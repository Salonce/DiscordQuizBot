package dev.salonce.discordquizbot.domain;

import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

//modifications to make:
//value object title for topic diff
//remove topic construction from domain
//batch answers sending

@Getter
public class Match{
    private String topic;
    private final int difficulty;
    private int curQuestionIndex = 0;
    private int inactivity = 0;
    private final Map<Long, Player> players = new LinkedHashMap<>();
    private final List<Question> questions;
    private MatchState matchState = MatchState.ENROLLMENT;

    public Match(List<Question> questions, String topic, int difficulty, Long ownerId){
        if (questions == null || questions.isEmpty() || topic == null || topic.isEmpty() || difficulty < 0 || ownerId == null) {
            throw new IllegalArgumentException("Wrong data passed to the match.");
        }
        this.questions = questions;
        players.put(ownerId, new Player(questions.size()));
        this.difficulty = difficulty;
    }

    public void addPlayer(Long userId) {
        if (matchState != MatchState.ENROLLMENT)
            throw new IllegalStateException("Cannot join now.");  // domain-level exception
        if (players.containsKey(userId))
            throw new IllegalStateException("Already joined.");
        players.put(userId, new Player(questions.size()));
    }

    public Map<Long, Long> getPlayersPoints() {
        Map<Long, Long> playerPoints = new LinkedHashMap<>();

        for (Map.Entry<Long, Player> entry : players.entrySet()) {
            Long playerId = entry.getKey();
            Player player = entry.getValue();

            long points = 0;
            for (int i = 0; i < questions.size(); i++) {
                int answerIndex = player.getAnswer(i);
                if (questions.get(i).isCorrectAnswer(answerIndex)) {
                    points++;
                }
            }
            playerPoints.put(playerId, points);
        }
        return playerPoints;
    }

    public void closeByOwner(){
        if (!isClosed())
            this.matchState = MatchState.CLOSED_BY_OWNER;
    }

    public boolean isCurrentQuestion(int index){
        return (index == curQuestionIndex);
    }

    public int getNumberOfQuestions(){
        return questions.size();
    }

    public Iterator<Long> getPlayersIdsIterator(){
        return players.keySet().iterator();
    }

    public void setPlayerAnswer(Long userId, int questionIndex, int answerIndex){
        players.get(userId).setAnswer(questionIndex, answerIndex);
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
//        if (matchState != MatchState.ANSWERING) {
//            throw new IllegalStateException("Cannot close answering if not in answering phase");
//        }
        this.matchState = MatchState.WAITING;
    }

    public boolean isClosed(){
        return ((matchState == MatchState.CLOSED_BY_INACTIVITY) || (matchState == MatchState.CLOSED_BY_OWNER));
    }

    public boolean everyoneAnswered(){
        for (Player player : players.values()){
            if (player.isUnanswered(curQuestionIndex))
                return false;
        }
        return true;
    }

    public void removeUser(Long userId){
        players.remove(userId);
    }

    public boolean isEnrollmentState(){
        return (this.matchState == MatchState.ENROLLMENT);
    }

    public boolean isAnsweringState(){
        return (this.matchState == MatchState.ANSWERING);
    }

    public boolean isClosedByOwnerState(){
        return (this.matchState == MatchState.CLOSED_BY_OWNER);
    }

    public boolean isClosedByInactivityState(){
        return (this.matchState == MatchState.CLOSED_BY_INACTIVITY);
    }

    public boolean isInTheMatch(Long userId){
        return players.containsKey(userId);
    }

    public Long getOwnerId(){
        try { return players.keySet().iterator().next(); }
        catch (NoSuchElementException e){ return null; }
    }

    public boolean isOwner(Long userId){
        return Objects.equals(userId, getOwnerId());
    }

    public Map<Integer, List<Long>> getPlayersGroupedByAnswer() {
        Map<Integer, List<Long>> groups = new HashMap<>();

        players.forEach((playerId, player) -> {
            int answer = player.getAnswer(curQuestionIndex);
            groups.computeIfAbsent(answer, k -> new ArrayList<>()).add(playerId);
        });

        return groups;
    }

    public Map<Integer, List<Long>> getPlayersGroupedByPoints() {
        return getPlayersPoints().entrySet().stream()
                .collect(Collectors.groupingBy(
                        e -> e.getValue().intValue(),   // points as key
                        Collectors.mapping(Map.Entry::getKey, Collectors.toList())
                ));
    }

    public void skipToNextQuestion(){
        curQuestionIndex++;
    }
    public boolean questionExists(){
        return curQuestionIndex < questions.size();
    }

    public Question getCurrentQuestion(){
        if (curQuestionIndex < questions.size())
            return questions.get(curQuestionIndex);
        else
            return null;
    }

    public void updateInactiveRounds(){
        int noAnswersCount = 0;
        for (Player player : players.values()){
            if (player.isUnanswered(curQuestionIndex))
                noAnswersCount++;
            else break;
        }

        if (noAnswersCount == players.size())
            inactivity++;
        else
            inactivity = 0;
    }

    public void closeIfInactiveLimitReached(int inactiveRoundsLimit){
        if (inactivity >= inactiveRoundsLimit) {
            matchState = MatchState.CLOSED_BY_INACTIVITY;
        }
    }
}
