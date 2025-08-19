package dev.salonce.discordquizbot.domain;

import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

//modifications to make:
//value object title for topic diff
//batch answers sending

@Getter
public class Match{
    private final String title;
    private final int difficulty;
    private final Map<Long, Player> players;
    private final Questions questions;
    private MatchState matchState;
    private Inactivity inactivity;

    public Match(Questions questions, String title, int difficulty, Long ownerId, Inactivity inactivity){
        if (questions == null || questions == null || title == null || title.isEmpty() || difficulty < 0 || ownerId == null) {
            throw new IllegalArgumentException("Wrong data passed to the match.");
        }
        this.title = title;
        this.questions = questions;
        this.difficulty = difficulty;
        this.matchState = MatchState.ENROLLMENT;
        this.players = new LinkedHashMap<>();
        this.players.put(ownerId, new Player(questions.size()));
        this.inactivity = inactivity;
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
                Answer answer = player.getAnswer(i);
                if (questions.get(i).isCorrectAnswer(answer)) {
                    points++;
                }
            }
            playerPoints.put(playerId, points);
        }
        return playerPoints;
    }

    public void closeByOwner(){
        if (!isAborted())
            this.matchState = MatchState.CLOSED_BY_OWNER;
    }

    public boolean isCurrentQuestion(int index){
        return (index == questions.getCurrentIndex());
    }

    public int getNumberOfQuestions(){
        return questions.size();
    }

    public Iterator<Long> getPlayersIdsIterator(){
        return players.keySet().iterator();
    }

    public void setPlayerAnswer(Long userId, int questionIndex, Answer answer){
        players.get(userId).setAnswer(questionIndex, answer);
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

    public boolean isAborted(){
        return ((matchState == MatchState.CLOSED_BY_INACTIVITY) || (matchState == MatchState.CLOSED_BY_OWNER));
    }

    public boolean isFinished(){
        return (matchState == MatchState.FINISHED);
    }

    public boolean everyoneAnswered(){
        for (Player player : players.values()){
            if (player.isUnanswered(questions.getCurrentIndex()))
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

    public Map<Answer, List<Long>> getPlayersGroupedByAnswer() {
        Map<Answer, List<Long>> groups = new HashMap<>();

        players.forEach((playerId, player) -> {
            Answer answer = player.getAnswer(questions.getCurrentIndex());
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

    public void nextQuestion(){
        if (!questions.next())
            matchState = MatchState.FINISHED;
    }

    public Question getCurrentQuestion(){
        return questions.current();
    }

    public int getCurrentQuestionIndex() {
        return questions.getCurrentIndex();
    }

    public Match updateInactiveRounds() {
        boolean allUnanswered = players.values().stream()
                .allMatch(player -> player.isUnanswered(questions.getCurrentIndex()));

        if (allUnanswered) inactivity.increment();else inactivity.reset();

        return this;
    }

    public void closeIfInactive(){
        if (inactivity.exceedsMax()) {
            matchState = MatchState.CLOSED_BY_INACTIVITY;
        }
    }
}
