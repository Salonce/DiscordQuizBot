package dev.salonce.discordquizbot.domain;

import dev.salonce.discordquizbot.domain.exceptions.UserAlreadyJoined;

import java.util.*;

public class Players {

    private final Map<Long, Player> players;

    public Players(){
        this.players = new HashMap<>();
    }

    public void add(Long userId, int answersSize) {
        if (players.containsKey(userId))
            throw new UserAlreadyJoined();
        players.put(userId, new Player(answersSize));
    }

    public boolean exists(Long userId){
        return players.containsKey(userId);
    }

    public Iterator<Long> getPlayersIdsIterator(){
        return players.keySet().iterator();
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

    public boolean nooneAnswered(int index) {
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
