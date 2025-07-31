package dev.salonce.discordquizbot.core.matches;

import discord4j.core.object.entity.channel.MessageChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class MatchService {

    private final MatchCache matchCache;
    private final MatchCreationService matchCreationService;

    public Match makeMatch(String topic, int difficulty, Long ownerId){
        return matchCreationService.makeMatch(topic, difficulty, ownerId);
    }

    public Match get(MessageChannel channel) {
        return matchCache.get(channel);
    }

    public void put(MessageChannel channel, Match match) {
        matchCache.put(channel, match);
    }

    public boolean containsKey(MessageChannel channel) {
        return matchCache.containsKey(channel);
    }

    public void remove(MessageChannel channel) {
        matchCache.remove(channel);
    }

    public Collection<Match> getAll() {
        return matchCache.getAll();
    }

}
