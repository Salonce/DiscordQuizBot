package dev.salonce.discordQuizBot.Core;

import lombok.Getter;

@Getter
public class Player{
    public Player(Long id){
        this.id = id;
        this.points = 0;
    }
    private final Long id;
    private int points;

    public void addPoint(){
        this.points++;
    }
}