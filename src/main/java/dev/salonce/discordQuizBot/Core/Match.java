package dev.salonce.discordQuizBot.Core;

import discord4j.core.object.entity.channel.MessageChannel;
import lombok.Getter;

import java.util.List;

@Getter
public class Match{

    private final List<Player> players;
    private final List<Question> questions;
    private final MessageChannel messageChannel;

    public Match(List<Question> questions, List<Player> players, MessageChannel messageChannel){
        this.questions = questions;
        this.players = players;
        this.messageChannel = messageChannel;
    }
}
