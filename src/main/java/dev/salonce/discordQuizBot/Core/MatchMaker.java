package dev.salonce.discordQuizBot.Core;

import discord4j.core.object.entity.channel.MessageChannel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class MatchMaker {

    private final QuestionFactory questionFactory;
    @Getter
    private final Set<MessageChannel> playingChannels;

    @Autowired
    public MatchMaker(QuestionFactory questionFactory){
        this.questionFactory = questionFactory;
        playingChannels = new HashSet<>();
    }

    public Match javaMatch(List<Player> players, MessageChannel messageChannel){
        List<Question> questions = questionFactory.javaQuestions();
        return new Match(questions, players, messageChannel);
    }

}
