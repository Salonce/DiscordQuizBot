package dev.salonce.discordQuizBot.Core;

import discord4j.core.object.entity.User;
import lombok.Getter;

@Getter
public class Player{
    public Player(User user){
        this.user = user;
        this.points = 0;
    }
    private final User user;
    private int points;

    public void addPoint(){
        this.points++;
    }
}