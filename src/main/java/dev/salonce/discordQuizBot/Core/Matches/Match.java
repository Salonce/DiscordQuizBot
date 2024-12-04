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
    @Setter
    private boolean enrolment;

    public Match(List<Question> questions){
        this.questions = questions;
        this.players = new HashMap<>();
        this.enrolment = true;
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
