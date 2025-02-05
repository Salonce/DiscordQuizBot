package dev.salonce.discordQuizBot.Core.Matches;

import dev.salonce.discordQuizBot.Core.Questions.Question;
import discord4j.core.object.entity.User;
import lombok.Getter;
import lombok.Setter;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class Match{
    private final Map<User, Player> players;
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
    private User owner;

    private String name;

    public boolean isClosed(){
        return enumMatchClosed != EnumMatchClosed.NOT_CLOSED;
    }

    public Match(List<Question> questions, String type, User owner, int unansweredQuestionsLimit){
        this.questions = questions;
        this.players = new HashMap<>();
        this.enrollment = true;
        this.currentQuestionNum = 0;
        this.noAnswerCount = 0;
        this.owner = owner;
        this.unansweredQuestionsLimit = unansweredQuestionsLimit;
        this.enumMatchClosed = EnumMatchClosed.NOT_CLOSED;

        if (type != null) {
            String capitalized = type.substring(0, 1).toUpperCase() + type.substring(1);
            this.name = capitalized;
        }

        players.put(owner, new Player(questions.size()));
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
        long matchOwnerId = this.owner.getId().asLong();
        if (matchOwnerId == ownerId){
            enumMatchClosed = EnumMatchClosed.BY_OWNER;
            return true;
        }
        return false;
    }

    //sort highest to lowest scores -> b - a
    public String getScoreboard(){
        return getPlayers().entrySet().stream().sorted((a, b) -> (b.getValue().getPoints() - a.getValue().getPoints())).map(entry -> "<@" + entry.getKey().getId().asString() + ">" + ": " + entry.getValue().getPoints()).collect(Collectors.joining("\n"));
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
                .map(entry -> "<@" + entry.getKey().getId().asString() + ">")
//              .map(entry -> "<@" + entry.getKey().getId().asString() + ">: " + maxPoints)
                .collect(Collectors.joining(", "));
    }

    public void addPlayerPoints(){
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

    private String getQuestionCorrectAnswerString(){
        return questions.get(currentQuestionNum).getCorrectAnswerString();
    }

    public void cleanPlayersAnswers(){
        for (Player player : players.values()){
            player.setCurrentAnswerNum(-1);
        }
    }

    public String getUsersAnswers(){
        List<List<String>> playersAnswers = new ArrayList<>();
        for (int i = 0; i < questions.get(currentQuestionNum).getAnswers().size() + 1; i++){
            playersAnswers.add(new ArrayList<>());
        }
        for (Map.Entry<User, Player> entry : players.entrySet()){
            int intAnswer = entry.getValue().getAnswersList().get(currentQuestionNum) + 1;
            //int intAnswer = entry.getValue().getCurrentAnswerNum() + 1;
            playersAnswers.get(intAnswer).add("<@" + entry.getKey().getId().asString() + ">");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < playersAnswers.size(); i++){
            if (i != 1) sb.append("\n");
            if (getQuestion().getCorrectAnswerInt() == i - 1)
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

    public boolean questionExists(){
        return currentQuestionNum < questions.size();
    }

    public Question getQuestion(){
        if (currentQuestionNum < questions.size())
            return questions.get(currentQuestionNum);
        else
            return null;
    }

    public void nextQuestion(){
        currentQuestionNum++;
    }

    public String getUserNames() {
        return players.keySet().stream()
                .sorted((u1, u2) -> {
                    if (u1 == owner) return -1;
                    if (u2 == owner) return 1;
                    return 0;  // Keep original order for non-owners
                })
                .map(user -> "<@" + user.getId().asString() + ">" +
                        (user == owner ? " (owner)" : ""))
                .collect(Collectors.joining(", "));
    }

//    public String getUserNames() {
//        return players.keySet().stream()
//                .map(user -> "<@" + user.getId().asString() + ">")
//                .collect(Collectors.joining(", "));
//    }

    public String addPlayer(User user, int questionsNumber){
        if (!isEnrollment()){
            return "Can't do that! You can join the match only during enrollment phase.";
        }
        if (players.containsKey(user)) {
            return "You've already joined the match.";
        }
        else {
            players.put(user, new Player(questionsNumber));
            if (players.size() == 1) {
                this.owner = user;
            }
            return "You've joined the match.";
        }
    }

    public String removePlayer(User user){
        if (!isEnrollment()){
            return "Can't do that! You can leave the match only during enrollment phase.";
        }
        if (isEnrollment() && players.containsKey(user)) {
            players.remove(user);
            //if player was the owner, remove his ownership
            if (this.owner != null && user.getId().asLong() == this.owner.getId().asLong()){
                //if there are players get a random user to be the new owner
                if (!players.isEmpty()){
                    this.owner = players.entrySet().iterator().next().getKey();
                }
                //if no players set owner to null
                else {
                    this.owner = null;
                }
            }
            return "You've left the match.";
        }
        else {
            return "You are not in the match to leave it.";
        }
    }
}
