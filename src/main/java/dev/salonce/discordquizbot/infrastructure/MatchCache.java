package dev.salonce.discordquizbot.infrastructure;

import dev.salonce.discordquizbot.domain.Match;
import discord4j.core.object.entity.channel.MessageChannel;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MatchCache {
    //private final Map<MessageChannel, Match> matches = new HashMap<>();
    private final Map<Long, Match> matches = new ConcurrentHashMap<>();

    public Match get(Long channelId) {
        return matches.get(channelId);
    }
    public void put(Long channelId, Match match) {
        matches.put(channelId, match);
    }
    public boolean containsKey(Long channelId) {
        return matches.containsKey(channelId);
    }
    public void remove(Long channelId) {
        matches.remove(channelId);
    }
    public Collection<Match> getAll() {
        return matches.values();
    }
}
