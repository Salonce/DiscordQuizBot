package dev.salonce.discordQuizBot.Core.Matches;

import dev.salonce.discordQuizBot.Core.Questions.Question;
import discord4j.core.object.entity.User;
import lombok.Getter;
import lombok.Setter;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class Match{
    private final Map<Long, Player> players;
    private final List<Question> questions;
    private final int unansweredQuestionsLimit;
    private int currentQuestionNum;
    private int noAnswerCount;



    private EnumMatchClosed enumMatchClosed;
    @Setter
    private boolean answeringOpen;
    @Setter
    private boolean enrollment;
    @Setter
    private Long ownerId;
    @Setter
    private boolean startNow;

    private String name;

    public boolean isClosed(){
        return enumMatchClosed != EnumMatchClosed.NOT_CLOSED;
    }

    public Match(List<Question> questions, String type, Long ownerId, int unansweredQuestionsLimit){
        this.questions = questions;
        this.players = new HashMap<>();
        this.enrollment = true;
        this.currentQuestionNum = 0;
        this.noAnswerCount = 0;
        this.ownerId = ownerId;
        this.unansweredQuestionsLimit = unansweredQuestionsLimit;
        this.enumMatchClosed = EnumMatchClosed.NOT_CLOSED;
        this.startNow = false;

        if (type != null) {
            this.name = type.substring(0, 1).toUpperCase() + type.substring(1); //Capitalize match name
        }

        players.put(ownerId, new Player(questions.size()));
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
                enumMatchClosed = EnumMatchClosed.BY_AUTOCLOSE;
            }
        }
        else
            noAnswerCount = 0;
    }

    public boolean closeMatch(Long ownerId){
        if (ownerId.equals(this.ownerId)){
            enumMatchClosed = EnumMatchClosed.BY_OWNER;
            return true;
        }
        return false;
    }

    //sort highest to lowest scores -> b - a
    public String getScoreboard(){
        return getPlayers().entrySet().stream().sorted((a, b) -> (b.getValue().getPoints() - a.getValue().getPoints())).map(entry -> "<@" + entry.getKey() + ">" + ": " + entry.getValue().getPoints()).collect(Collectors.joining("\n"));
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
        return players.keySet().stream()
                .sorted((u1, u2) -> {
                    if (u1.equals(ownerId)) return -1;
                    if (u2.equals(ownerId)) return 1;
                    return 0;  // Keep original order for non-owners
                })
                .map(userId -> "<@" + userId + ">" +
                        (userId.equals(ownerId) ? " (owner)" : ""))
                .collect(Collectors.joining(", "));
    }

    public String addPlayer(Long userId, int questionsNumber){
        if (!isEnrollment()){
            return "Can't do that! You can join the match only during enrollment phase.";
        }
        if (players.containsKey(userId)) {
            return "You've already joined the match.";
        }
        else {
            players.put(userId, new Player(questionsNumber));
            if (players.size() == 1) {
                this.ownerId = userId;
            }
            return "You've joined the match.";
        }
    }

    public String removePlayer(Long userId){
        if (!isEnrollment()){
            return "Can't do that! You can leave the match only during enrollment phase.";
        }
        if (isEnrollment() && players.containsKey(userId)) {
            players.remove(userId);
            //if player was the owner, remove his ownership
            if (userId.equals(ownerId)){
                this.ownerId = null;
                //if there are players get a random user to be the new owner
                if (!players.isEmpty()){
                    this.ownerId = players.entrySet().iterator().next().getKey();
                }
            }
            return "You've left the match.";
        }
        else {
            return "You are not in the match to leave it.";
        }
    }
}
