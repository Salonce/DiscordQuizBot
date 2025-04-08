package dev.salonce.discordQuizBot.Core.Matches;

import dev.salonce.discordQuizBot.Core.Questions.Question;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class Match{
    private String name;
    private final Map<Long, Player> players = new LinkedHashMap<>();;
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

    public void updateInactiveCountAndCloseMatchIfLimit(){
        int noAnswersCount = 0;
        for (Player player : players.values()){
            int intAnswer = player.getAnswersList().get(currentQuestionNum);
            if (intAnswer == -1)
                noAnswersCount++;
            else break;
        }

        if (noAnswersCount == players.size()) {
            inactiveRounds++;
            if (inactiveRounds >= inactiveRoundsLimit) {
                matchState = MatchState.CLOSED_BY_INACTIVITY;
            }
        }
        else
            inactiveRounds = 0;
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

    public String getUsersAnswers(){
        List<List<String>> playersAnswers = new ArrayList<>();
        for (int i = 0; i < questions.get(currentQuestionNum).getAnswers().size() + 1; i++){
            playersAnswers.add(new ArrayList<>());
        }
        for (Map.Entry<Long, Player> entry : players.entrySet()){
            int intAnswer = entry.getValue().getAnswersList().get(currentQuestionNum) + 1;
            //int intAnswer = entry.getValue().getCurrentAnswerNum() + 1;
            playersAnswers.get(intAnswer).add("<@" + entry.getKey().toString() + ">");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < playersAnswers.size(); i++){
            if (i != 1) sb.append("\n");
            if (getCurrentQuestion().getCorrectAnswerInt() == i - 1)
                sb.append("✅ ").append("**").append((char)('A' + i - 1)).append("**: ");
            else
                sb.append("❌ ").append((char)('A' + i - 1)).append(": ");
            for (int j = 0; j < playersAnswers.get(i).size(); j++){
                if (j != 0) sb.append(", ");
                sb.append(playersAnswers.get(i).get(j));
            }
        }
        sb.append("\n❌ ").append("-: ");
        for (int j = 0; j < playersAnswers.get(0).size(); j++){
            if (j != 0) sb.append(", ");
            sb.append(playersAnswers.get(0).get(j));
        }
        return sb.toString();
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

    public String getUserNames() {
        Iterator<Long> iterator = players.keySet().iterator();
        if (!iterator.hasNext())
            return "";
        Long ownerId = iterator.next();
        StringBuilder result = new StringBuilder("<@" + ownerId + "> (owner)");
        while (iterator.hasNext())
            result.append(", <@").append(iterator.next()).append(">");
        return result.toString();
    }

    //sort highest to lowest scores -> b - a
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


    public String getWinners() {
        // Find the max points
        int maxPoints = getPlayers().values().stream()
                .mapToInt(Player::getPoints)
                .max()
                .orElse(0);

        // Get all players with max points
        return getPlayers().entrySet().stream()
                .filter(entry -> entry.getValue().getPoints() == maxPoints)
                .map(entry -> "<@" + entry.getKey().toString() + ">")
//              .map(entry -> "<@" + entry.getKey().getId().asString() + ">: " + maxPoints)
                .collect(Collectors.joining(", "));
    }
}
