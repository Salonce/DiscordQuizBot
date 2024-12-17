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
    private int questionNumber;
    @Setter
    private boolean enrolment;

    public String getScoreboard(){
        return getPlayers().entrySet().stream().sorted((a, b) -> (a.getValue().getPoints() - b.getValue().getPoints())).map(entry -> "<@" + entry.getKey().getId().asString() + ">" + ": " + entry.getValue().getPoints()).collect(Collectors.joining("\n"));
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
            if (player.getCurrentAnswerNum() == getQuestionCorrectAnswerInt())
                player.addPoint();
        }
    }

    private int getQuestionCorrectAnswerInt(){
        return questions.get(questionNumber).getCorrectAnswerInt();
    }

    private String getQuestionCorrectAnswerString(){
        return questions.get(questionNumber).getCorrectAnswerString();
    }

//    private List<Integer> getQuestionCorrectAnswerInt(){
//        return questions.get(questionNumber).getCorrectAnswerListInt();
//    }

//    public String getScoreBoard() {
//    }

    public void cleanPlayersAnswers(){
        for (Player player : players.values()){
            player.setCurrentAnswerNum(-1);
        }
    }


    //change only this list to show
    public String getUsersAnswers(){
        List<List<String>> playersAnswers = new ArrayList<>();
        for (int i = 0; i < questions.get(questionNumber).getAnswers().size() + 1; i++){
            playersAnswers.add(new ArrayList<>());
        }
        for (Map.Entry<User, Player> entry : players.entrySet()){
            int intAnswer = entry.getValue().getCurrentAnswerNum() + 1;
            playersAnswers.get(intAnswer).add("<@" + entry.getKey().getId().asString() + ">");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < playersAnswers.size(); i++){
            if (i != 1) sb.append("\n");
            sb.append((char)('A' + i - 1)).append(": ");
            for (int j = 0; j < playersAnswers.get(i).size(); j++){
                if (j != 0) sb.append(", ");
                sb.append(playersAnswers.get(i).get(j));
            }
        }
        sb.append("\nNone: ");
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

    public Match(List<Question> questions){
        this.questions = questions;
        this.players = new HashMap<>();
        this.enrolment = true;
        this.questionNumber = 0;
    }

    public boolean addPlayer(User user){
        if (players.containsKey(user)) {
            //System.out.println("User is already on player list.");
            return false;
        }
        else {
            players.put(user, new Player());
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
