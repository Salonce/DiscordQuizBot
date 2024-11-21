package dev.salonce.discordQuizBot.Core;

import discord4j.core.object.entity.channel.MessageChannel;
import lombok.Getter;

import java.util.List;

@Getter
public class Match implements Runnable{

    private final List<Player> players;
    private final List<Question> questions;
    private final MessageChannel messageChannel;

    public void start() {
    }

    public Match(List<Question> questions, List<Player> players, MessageChannel messageChannel){
        this.questions = questions;
        this.players = players;
        this.messageChannel = messageChannel;
    }

    @Override
    public void run() {

    }
}
