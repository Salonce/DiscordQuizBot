package dev.salonce.discordquizbot.domain;

import dev.salonce.discordquizbot.domain.exceptions.UserAlreadyJoined;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.NoSuchElementException;

public class Players {

    private final Map<Long, Player> players;

    public Players(){
        this.players = new HashMap<>();
    }

    public void addPlayer(Long userId, int annswersSize) {
        if (players.containsKey(userId))
            throw new UserAlreadyJoined();
        players.put(userId, new Player(annswersSize));
    }

    public List<Long> getPlayersIds(){
        return players.keySet().stream().toList();
    }

    public Long getOwnerId(){
        try { return players.keySet().iterator().next(); }
        catch (NoSuchElementException e){ return null; }
    }

    public void setPlayerAnswer(Long userId, int questionIndex, Answer answer){
        players.get(userId).setAnswer(questionIndex, answer);
    }

    public void removePlayer(Long userId){
        players.remove(userId);
    }

    public boolean isInTheMatch(Long userId){
        return players.containsKey(userId);
    }

    private boolean nooneAnswered(int index) {
        return players.values().stream()
                .allMatch(player -> player.isUnanswered(index));
    }

    public boolean everyoneAnswered(int index){
        for (Player player : players.values()){
            if (player.isUnanswered(index))
                return false;
        }
        return true;
    }


}
