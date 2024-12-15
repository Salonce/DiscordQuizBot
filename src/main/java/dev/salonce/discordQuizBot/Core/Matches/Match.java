package dev.salonce.discordQuizBot.Core.Matches;

import dev.salonce.discordQuizBot.Core.Questions.Question;
import discord4j.core.object.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public String getUserNames(){
        List<String> users = players.keySet().stream().map(User::getUsername).toList();
        StringBuilder userNamesBuilder = new StringBuilder();
        for (int i = 0; i < users.size() - 1; i++){
                userNamesBuilder.append(users.get(i)).append(", ");
        }
        if (users.size() > 0) {
            userNamesBuilder.append(users.get(users.size() - 1));
        }
        return userNamesBuilder.toString();

        //String userNames = players.keySet().toString();
        //return userNames;
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
