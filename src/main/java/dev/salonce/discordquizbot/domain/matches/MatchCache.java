package dev.salonce.discordquizbot.domain.matches;

import discord4j.core.object.entity.channel.MessageChannel;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MatchCache {
    //private final Map<MessageChannel, Match> matches = new HashMap<>();
    private final Map<MessageChannel, Match> matches = new ConcurrentHashMap<>();

    Match get(MessageChannel channel) {
        return matches.get(channel);
    }

    void put(MessageChannel channel, Match match) {
        matches.put(channel, match);
    }

    boolean containsKey(MessageChannel channel) {
        return matches.containsKey(channel);
    }

    void remove(MessageChannel channel) {
        matches.remove(channel);
    }

    Collection<Match> getAll() {
        return matches.values();
    }
}
