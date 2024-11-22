package dev.salonce.discordQuizBot.Core.Matches;

import dev.salonce.discordQuizBot.Core.Questions.Question;
import dev.salonce.discordQuizBot.Core.Questions.QuestionFactory;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class MatchFactory {

    private final QuestionFactory questionFactory;
    @Getter
    private final Set<MessageChannel> playingChannels;

    @Autowired
    public MatchFactory(QuestionFactory questionFactory){
        this.questionFactory = questionFactory;
        playingChannels = new HashSet<>();
    }

    public Match javaMatch(List<Player> players, MessageChannel messageChannel){
        List<Question> questions = questionFactory.javaQuestions();
        return new Match(questions, players, messageChannel);
    }

}
