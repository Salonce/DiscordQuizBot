package dev.salonce.discordQuizBot.Core.Matches;

import dev.salonce.discordQuizBot.Core.Questions.Question;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class Match{

    //could add rounds list (with updates) and then just calculate back on "update"

    private String name;
    private final Map<Long, Player> players = new LinkedHashMap<>();
    private final List<Question> questions;
    private final int inactiveRoundsLimit;
    private int currentQuestionNum = 0;
    private int inactiveRounds = 0;
    private MatchState matchState = MatchState.ENROLLMENT;

    public Match(List<Question> questions, String name, Long ownerId, int inactiveRoundsLimit){
        this.questions = questions;
        this.inactiveRoundsLimit = inactiveRoundsLimit;
        players.put(ownerId, new Player(questions.size()));

        if (name != null) {
            this.name = name.substring(0, 1).toUpperCase() + name.substring(1); //Capitalize match name
        }
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

    public void updateScores(){
        for (Player player : players.values()){
            if (player.getAnswersList().get(currentQuestionNum) == getCurrentQuestionCorrectAnswer())
                player.addPoint();
        }
    }

    private int getCurrentQuestionCorrectAnswer(){
        return questions.get(currentQuestionNum).getCorrectAnswerInt();
    }

    public void switchStateToClosedIfInactiveRoundsInARowLimitReached(){
        if (inactiveRounds >= inactiveRoundsLimit) {
            matchState = MatchState.CLOSED_BY_INACTIVITY;
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

    //sorted highest to lowest scores -> b - a
    public String getScoreboard(){
        return getPlayers().entrySet().stream().sorted((a, b) -> (b.getValue().getPoints() - a.getValue().getPoints())).map(entry -> "<@" + entry.getKey() + ">" + ": " + entry.getValue().getPoints() + " points").collect(Collectors.joining("\n"));
    }

    public String getFinalScoreboard() {
        // Group players by their points
        Map<Integer, List<String>> pointsGrouped = getPlayers().entrySet().stream()
                .collect(Collectors.groupingBy(
                        entry -> entry.getValue().getPoints(),
                        Collectors.mapping(entry -> "<@" + entry.getKey() + ">", Collectors.toList())
                ));

        // Sort the points in descending order
        List<Integer> sortedPoints = pointsGrouped.keySet().stream()
                .sorted((a, b) -> b - a) // Sorting points in descending order
                .collect(Collectors.toList());

        // Build the scoreboard message
        StringBuilder scoreboard = new StringBuilder();
        int place = 1;

        for (Integer points : sortedPoints) {
            List<String> players = pointsGrouped.get(points);
            String playersList = String.join(", ", players);
            scoreboard.append(getOrdinalSuffix(place)).append(" place: ").append(playersList)
                    .append(" : ").append(points).append(" points\n");
            place++;
        }

        return scoreboard.toString().trim();
    }

    // Helper method to get the ordinal suffix (1st, 2nd, 3rd, etc.)
    private String getOrdinalSuffix(int place) {
        if (place % 100 >= 11 && place % 100 <= 13) {
            return place + "th";
        }
        switch (place % 10) {
            case 1: return place + "st";
            case 2: return place + "nd";
            case 3: return place + "rd";
            default: return place + "th";
        }
    }
}
