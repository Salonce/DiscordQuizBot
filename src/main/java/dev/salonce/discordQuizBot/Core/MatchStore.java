package dev.salonce.discordQuizBot.Core;

import dev.salonce.discordQuizBot.Core.Matches.Match;
import discord4j.core.object.entity.channel.MessageChannel;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MatchStore {
    //private final Map<MessageChannel, Match> matches = new HashMap<>();
    private final Map<MessageChannel, Match> matches = new ConcurrentHashMap<>();

    public Match get(MessageChannel channel) {
        return matches.get(channel);
    }

    public void put(MessageChannel channel, Match match) {
        matches.put(channel, match);
    }

    public boolean containsKey(MessageChannel channel) {
        return matches.containsKey(channel);
    }

    public void remove(MessageChannel channel) {
        matches.remove(channel);
    }

    public Collection<Match> getAll() {
        return matches.values();
    }
}
