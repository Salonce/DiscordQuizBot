package dev.salonce.discordQuizBot.Core.Matches;

import dev.salonce.discordQuizBot.Core.Questions.Question;
import discord4j.core.object.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class Match{

    private final Map<User, Player> players;
    private final List<Question> questions;
    private int questionNumber;
    @Setter
    private boolean enrolment;

    public boolean quizEnd(){
        return questionNumber > questions.size() - 1;
    }

    public Question getQuestion(){
        if (questionNumber <= questions.size())
            return questions.get(questionNumber);
        else
            return null;
    }

    public boolean nextQuestion(){
        if (++questionNumber <= questions.size() - 1)
            return true;
        else
            return false;
    }

    public String getUserNames() {
        StringBuilder userNames = new StringBuilder();
        for (User user : players.keySet()) {
            if (userNames.length() > 0) {
                userNames.append(", ");
            }
            userNames.append(user.getUsername());
        }
        return userNames.toString();
    }

//    public String getUserNames() {
//        System.out.println(players.keySet().stream()
//                .map(User::getUsername)
//                .collect(Collectors.joining(", ")));
//        return players.keySet().stream()
//                .map(User::getUsername)
//                .collect(Collectors.joining(", "));
//    }

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
