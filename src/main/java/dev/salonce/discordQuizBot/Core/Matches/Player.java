package dev.salonce.discordQuizBot.Core.Matches;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Player {
    public Player(){
        this.points = 0;
    }
    private int points;

    @Setter
    private Character currentAnswer = '0';

    @Setter
    private Integer currentAnswerNum = 0;

    public void addPoint(){
        this.points++;
    }
}