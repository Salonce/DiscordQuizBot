package dev.salonce.discordQuizBot.Core.Matches;

import dev.salonce.discordQuizBot.Configs.QuizConfig;
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
    private int questionNumber;
    private int noAnswerCount;

    private boolean isClosed;
    @Setter
    private boolean answeringOpen;
    @Setter
    private boolean enrolment;
    @Setter
    private Long ownerId;

    private String name;

    private final QuizConfig quizConfig;

    public Match(List<Question> questions, String type, Long ownerId, QuizConfig quizConfig){
        this.questions = questions;
        this.players = new HashMap<>();
        this.enrolment = true;
        this.questionNumber = 0;
        this.isClosed = false;
        this.ownerId = ownerId;
        this.noAnswerCount = 0;
        this.quizConfig = quizConfig;

        if (type != null) {
            String capitalized = type.substring(0, 1).toUpperCase() + type.substring(1);
            this.name = capitalized;
        }
    }

    public void setNoAnswerCountAndCloseMatchIfLimit(){
        int noAnswersCount = 0;
        for (Player player : players.values()){
            int intAnswer = player.getAnswersList().get(questionNumber);
            if (intAnswer == -1)
                noAnswersCount++;
            else break;
        }

        if (noAnswersCount == players.size()) {
            noAnswerCount++;
            if (noAnswerCount >= quizConfig.getUnansweredLimit())
                isClosed = true;
        }
        else
            noAnswerCount = 0;
    }

    public boolean closeMatch(Long ownerId){
        if (this.ownerId.equals(ownerId)){
            isClosed = true;
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
            if (player.getAnswersList().get(questionNumber) == getQuestionCorrectAnswerInt())
                player.addPoint();
//            if (player.getCurrentAnswerNum() == getQuestionCorrectAnswerInt())
//                player.addPoint();
        }
    }

    private int getQuestionCorrectAnswerInt(){
        return questions.get(questionNumber).getCorrectAnswerInt();
    }

    private String getQuestionCorrectAnswerString(){
        return questions.get(questionNumber).getCorrectAnswerString();
    }

    public void cleanPlayersAnswers(){
        for (Player player : players.values()){
            player.setCurrentAnswerNum(-1);
        }
    }

    public String getUsersAnswers(){
        List<List<String>> playersAnswers = new ArrayList<>();
        for (int i = 0; i < questions.get(questionNumber).getAnswers().size() + 1; i++){
            playersAnswers.add(new ArrayList<>());
        }
        for (Map.Entry<User, Player> entry : players.entrySet()){
            int intAnswer = entry.getValue().getAnswersList().get(questionNumber) + 1;
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
        return questionNumber < questions.size();
    }

    public Question getQuestion(){
        if (questionNumber < questions.size())
            return questions.get(questionNumber);
        else
            return null;
    }

    public void nextQuestion(){
        questionNumber++;
    }

    public String getUserNames() {
        return players.keySet().stream()
                //.map(User::getUsername)
                .map(user -> "<@" + user.getId().asString() + ">")
                .collect(Collectors.joining(", "));
    }

    public boolean addPlayer(User user, int questionsNumber){
        if (players.containsKey(user)) {
            //System.out.println("User is already on player list.");
            return false;
        }
        else {
            players.put(user, new Player(questionsNumber));
            return true;
        }
    }

    public boolean removePlayer(User user){
        if (players.containsKey(user)) {
            players.remove(user);
            return true;
        }
        else {
            //System.out.println("User is not on the player list.");
            return false;
        }
    }



}
