package dev.salonce.discordquizbot.domain;

import dev.salonce.discordquizbot.core.questions.questions.Question;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.NoSuchElementException;

@Getter
public class Match{

    //could maybe add rounds list (with updates) and then just calculate back on "update"

    private String topic;
    private final int difficulty;
    private final Map<Long, Player> players = new LinkedHashMap<>();
    private final List<Question> questions;
    private final int inactiveRoundsLimit;
    private int currentQuestionNum = 0;
    private int inactiveRounds = 0;
    private MatchState matchState = MatchState.ENROLLMENT;

    public Match(List<Question> questions, String topic, int difficulty, Long ownerId, int inactiveRoundsLimit){
        this.questions = questions;
        this.inactiveRoundsLimit = inactiveRoundsLimit;
        players.put(ownerId, new Player(questions.size()));

        if (topic != null) {
            this.topic = topic.substring(0, 1).toUpperCase() + topic.substring(1); //Capitalize match name
        }
        this.difficulty = difficulty;
    }

    public void addPlayer(Long userId){
        players.put(userId, new Player(questions.size()));
    }

    public void setMatchState(MatchState matchState) {
        if (!isClosed()) this.matchState = matchState;
    }

    public boolean isClosed(){
        return ((matchState == MatchState.CLOSED_BY_INACTIVITY) || (matchState == MatchState.CLOSED_BY_OWNER));
    }

    public boolean everyoneAnswered(){
        for (Player player : players.values()){
            if (player.getAnswersList().get(currentQuestionNum) == -1)
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
            if (player.getAnswersList().get(currentQuestionNum) == getCurrentQuestionCorrectAnswer())
                player.addPoint();
        }
    }

    private int getCurrentQuestionCorrectAnswer(){
        return questions.get(currentQuestionNum).getCorrectAnswerInt();
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

    //actually adds +1 inactive round if current round was inactive, repeating this function in the same round will make results wrong
    public void updateInactiveRoundsInARowCount(){
        int noAnswersCount = 0;
        for (Player player : players.values()){
            int intAnswer = player.getAnswersList().get(currentQuestionNum);
            if (intAnswer == -1)
                noAnswersCount++;
            else break;
        }

        if (noAnswersCount == players.size())
            inactiveRounds++;
        else
            inactiveRounds = 0;
    }

    public void switchStateToClosedIfInactiveRoundsInARowLimitReached(){
        if (inactiveRounds >= inactiveRoundsLimit) {
            matchState = MatchState.CLOSED_BY_INACTIVITY;
        }
    }
}
