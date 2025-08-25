package dev.salonce.discordquizbot.domain;

import dev.salonce.discordquizbot.domain.exceptions.NotEnrollmentState;
import dev.salonce.discordquizbot.domain.exceptions.UserAlreadyJoined;
import lombok.Getter;

import java.util.*;

//modifications to make:
//value object title for topic diff
//batch answers sending

@Getter
public class Match{
    private final String title;
    private final int difficulty;
    private final Players players;
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
        this.players = new Players();
        this.players.add(ownerId, questions.size());
        this.inactivity = inactivity;
    }

    public void addPlayer(Long userId) {
        if (matchState != MatchState.ENROLLMENT)
            throw new NotEnrollmentState();
        if (players.exists(userId))
            throw new UserAlreadyJoined();
        players.add(userId, questions.size());
    }

    public Iterator<Long> getPlayersIdsIterator(){
        return players.getPlayersIdsIterator();
    }

    public void setPlayerAnswer(Long userId, int questionIndex, Answer answer){
        players.setPlayerAnswer(userId, questionIndex, answer);
    }

    public void removeUser(Long userId){
        players.removePlayer(userId);
    }

    public boolean playerExists(Long userId){
        return players.exists(userId);
    }

    public Long getOwnerId(){
        return players.getOwnerId();
    }

    private boolean allPlayersUnanswered() {
        return players.nooneAnswered(currentQuestionIndex());
    }

    public boolean everyoneAnsweredCurrentQuestion(){
        return players.everyoneAnswered(currentQuestionIndex());
    }

    public void abortByOwner(){
        if (!isAborted())
            this.matchState = MatchState.ABORTED_BY_OWNER;
    }

    public boolean isCurrentQuestion(int index){
        return (index == questions.getCurrentIndex());
    }

    public int getNumberOfQuestions(){
        return questions.size();
    }

    public void startAnsweringPhase() {
//        if (matchState != MatchState.COUNTDOWN) {
//            throw new IllegalStateException("Cannot close answering if not in countdown phase");
//        }
        this.matchState = MatchState.QUESTION;
    }

    public void startCountdownPhase(){
        this.matchState = MatchState.STARTING;
    }

    public void startBetweenQuestionsPhase() {
//        if (matchState != MatchState.ANSWERING) {
//            throw new IllegalStateException("Cannot close answering if not in answering phase");
//        }
        this.matchState = MatchState.BETWEEN_QUESTIONS;
    }

    public boolean isStarting(){
        return (matchState == MatchState.STARTING);
    }

    public boolean isAborted(){
        return ((matchState == MatchState.ABORTED_BY_INACTIVITY) || (matchState == MatchState.ABORTED_BY_OWNER));
    }

    public boolean isFinished(){
        return (matchState == MatchState.FINISHED);
    }

    public boolean isEnrolling(){
        return (this.matchState == MatchState.ENROLLMENT);
    }

    public boolean isAnsweringState(){
        return (this.matchState == MatchState.QUESTION);
    }

    public boolean isAbortedByOwner(){
        return (this.matchState == MatchState.ABORTED_BY_OWNER);
    }

    public boolean isAbortedByInactivity(){
        return (this.matchState == MatchState.ABORTED_BY_INACTIVITY);
    }


    public boolean isOwner(Long userId){
        return Objects.equals(userId, getOwnerId());
    }

    public void nextQuestion(){
        if (!questions.next())
            matchState = MatchState.FINISHED;
    }

    public Question getCurrentQuestion(){
        return questions.current();
    }

    public int currentQuestionIndex() {
        return questions.getCurrentIndex();
    }

    public void checkInactivity() {
        if (allPlayersUnanswered()) {
            inactivity.increment();
            if (inactivity.exceedsMax()) {
                matchState = MatchState.ABORTED_BY_INACTIVITY;
            }
        } else {
            inactivity.reset();
        }
    }

    public Map<Answer, List<Long>> getPlayersGroupedByAnswer() {
        return players.getPlayersGroupedByAnswer(currentQuestionIndex());
    }

    public Scoreboard getScoreboard() {
        return new Scoreboard(getPlayersScores());
    }

    private List<PlayerScore> getPlayersScores() {
        List<Answer> correctAnswers = questions.getCorrectAnswersList();
        List<PlayerScore> scores = new ArrayList<>();

        for (Map.Entry<Long, Player> entry : players.getPlayersMap().entrySet()) {
            scores.add(new PlayerScore(
                    entry.getKey(),
                    entry.getValue().calculateScore(correctAnswers)
            ));
        }

        return scores;
    }

    public AnswerDistribution getAnswerGroups() {
        List<Answer> possibleAnswers = questions.current().getPossibleAnswers();

        List<AnswerOptionGroup> answerOptionGroupList = possibleAnswers.stream()
                .map(answer -> players.getAnswerGroup(currentQuestionIndex(), answer))
                .toList();

        return new AnswerDistribution(answerOptionGroupList);
    }

}
