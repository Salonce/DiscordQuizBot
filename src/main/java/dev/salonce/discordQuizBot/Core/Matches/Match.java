package dev.salonce.discordQuizBot.Core.Matches;

import dev.salonce.discordQuizBot.Core.Questions.Question;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Match{

    private final List<Player> players;
    private final List<Question> questions;

    public Match(List<Question> questions){
        this.questions = questions;
        this.players = new ArrayList<>();
    }


}
