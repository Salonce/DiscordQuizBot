package dev.salonce.discordQuizBot.Core;

import lombok.Getter;

import java.util.List;

@Getter
public class Match implements Runnable{

    private final List<Player> players;
    private final List<Question> questions;

    public void start() {
    }

    public Match(List<Question> questions, List<Player> players){
        this.questions = questions;
        this.players = players;
    }

    @Override
    public void run() {

    }
}
