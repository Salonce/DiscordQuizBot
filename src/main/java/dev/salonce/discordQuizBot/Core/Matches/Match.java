package dev.salonce.discordQuizBot.Core.Matches;

import dev.salonce.discordQuizBot.Core.Questions.Question;
import lombok.Getter;
import lombok.Setter;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class Match{
    private final Map<Long, Player> players = new LinkedHashMap<>();;
    private final List<Question> questions;
    private final int unansweredQuestionsLimit;
    private int currentQuestionNum = 0;
    private int noAnswerCount = 0;

    @Setter
    private MatchState matchState = MatchState.ENROLLMENT;;
    @Setter
    private boolean answeringOpen;
    @Setter
    private boolean startNow = false;

    private String name;

    public boolean isClosed(){
        return ((matchState == MatchState.CLOSED_BY_INACTIVITY) || (matchState == MatchState.CLOSED_BY_OWNER));
    }

    public Match(List<Question> questions, String type, Long ownerId, int unansweredQuestionsLimit){
        this.questions = questions;
        this.unansweredQuestionsLimit = unansweredQuestionsLimit;
        players.put(ownerId, new Player(questions.size()));

        if (type != null) {
            this.name = type.substring(0, 1).toUpperCase() + type.substring(1); //Capitalize match name
        }
    }

    public boolean everyoneAnswered(){
        for (Player player : players.values()){
            if (player.getAnswersList().get(currentQuestionNum) == -1)
                return false;
        }
        return true;
    }

    public void setNoAnswerCountAndCloseMatchIfLimit(){
        int noAnswersCount = 0;
        for (Player player : players.values()){
            int intAnswer = player.getAnswersList().get(currentQuestionNum);
            if (intAnswer == -1)
                noAnswersCount++;
            else break;
        }

        if (noAnswersCount == players.size()) {
            noAnswerCount++;
            if (noAnswerCount >= unansweredQuestionsLimit) {
                matchState = MatchState.CLOSED_BY_INACTIVITY;
            }
        }
        else
            noAnswerCount = 0;
    }

    public boolean closeMatch(Long userId){
        if (userId.equals(getOwnerId())){
            matchState = MatchState.CLOSED_BY_OWNER;
            return true;
        }
        return false;
    }

    private Long getOwnerId(){
        try { return players.keySet().iterator().next(); }
        catch (NoSuchElementException e){ return null; }
    }

    public void updatePlayerPoints(){
        for (Player player : players.values()){
            if (player.getAnswersList().get(currentQuestionNum) == getQuestionCorrectAnswerInt())
                player.addPoint();
//            if (player.getCurrentAnswerNum() == getQuestionCorrectAnswerInt())
//                player.addPoint();
        }
    }

    private int getQuestionCorrectAnswerInt(){
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

    public String addPlayer(Long userId, int questionsNumber){
        if (matchState != MatchState.ENROLLMENT){
            return "Can't do that! You can join the match only during enrollment phase.";
        }
        if (players.containsKey(userId)) {
            return "You've already joined the match.";
        }
        else {
            players.put(userId, new Player(questionsNumber));
            return "You've joined the match.";
        }
    }

    public String removePlayer(Long userId){
        if (matchState != MatchState.ENROLLMENT) {
            return "Can't do that! You can leave the match only during enrollment phase.";
        }
        else if (matchState == MatchState.ENROLLMENT && players.containsKey(userId)) {
            players.remove(userId);
            return "You've left the match.";
        }
        else {
            return "You are not in the match to leave it.";
        }
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

//    public String getScoreboard(){
//        return getPlayers().entrySet().stream().sorted((a, b) -> (b.getValue().getPoints() - a.getValue().getPoints())).map(entry -> "<@" + entry.getKey() + ">" + ": " + entry.getValue().getPoints()).collect(Collectors.joining("\n"));
//    }


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
