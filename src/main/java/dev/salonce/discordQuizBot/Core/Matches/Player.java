package dev.salonce.discordQuizBot.Core.Matches;

import lombok.Getter;

@Getter
public class Player {
    public Player(){
        this.points = 0;
    }
    private int points;

    public void addPoint(){
        this.points++;
    }
}