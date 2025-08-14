package dev.salonce.discordquizbot.application;

import dev.salonce.discordquizbot.domain.Match;
import dev.salonce.discordquizbot.domain.MatchState;
import dev.salonce.discordquizbot.infrastructure.MatchCache;
import dev.salonce.discordquizbot.infrastructure.buttons.ButtonInteractionData;
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



    public String addPlayerToMatch(MessageChannel channel, Long userId) {
        Match match = get(channel);

        if (match.getMatchState() != MatchState.ENROLLMENT) {
            return "You can join only during enrollment phase.";
        }
        if (match.getPlayers().containsKey(userId)) {
            return "You’ve already joined the match.";
        }

        match.addPlayer(userId);
        return "You’ve joined the match.";
    }

    public String cancelMatch (MessageChannel messageChannel, Long userId) {
        Match match = get(messageChannel);
        if (match == null)
            return "This match doesn't exist anymore.";
        if (!match.getOwnerId().equals(userId))
            return "You are not the owner. Only the owner can cancel the match.";
        match.setMatchState(MatchState.CLOSED_BY_OWNER);
        return "With your undeniable power of ownership, you've cancelled the match";
    }
}
