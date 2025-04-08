package dev.salonce.discordQuizBot.Core.MessagesSending;

import dev.salonce.discordQuizBot.Core.MatchStore;
import dev.salonce.discordQuizBot.Core.Matches.Match;
import dev.salonce.discordQuizBot.Core.Matches.Player;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class MatchResultsMessage {

    private final MatchStore matchStore;

    public Mono<Message> create(MessageChannel messageChannel){
        Match match = matchStore.get(messageChannel);

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("Final scoreboard: " )
                .description(getFinalScoreboard(match))
                //.addField("\uD83C\uDFC6", "The winners are: " + match.getWinners(), false)
                .build();

        return messageChannel.createMessage(embed);
    }

    private String getFinalScoreboard(Match match) {
        Map<Long, Player> playersMap = match.getPlayers();

        // Group players by their points
        Map<Integer, List<String>> pointsGrouped = playersMap.entrySet().stream()
                .collect(Collectors.groupingBy(
                        entry -> entry.getValue().getPoints(),
                        Collectors.mapping(entry -> "<@" + entry.getKey() + ">", Collectors.toList())
                ));

        // Sort the points in descending order
        List<Integer> sortedPoints = pointsGrouped.keySet().stream()
                .sorted((a, b) -> b - a) // Sorting points in descending order
                .collect(Collectors.toList());

        // Build the scoreboard message
        StringBuilder scoreboard = new StringBuilder();
        int place = 1;

        for (Integer points : sortedPoints) {
            List<String> players = pointsGrouped.get(points);
            String playersList = String.join(", ", players);
            scoreboard.append(getOrdinalSuffix(place)).append(" place: ").append(playersList)
                    .append(" : ").append(points).append(" points\n");
            place++;
        }

        return scoreboard.toString().trim();
    }

    // Helper method to get the ordinal suffix (1st, 2nd, 3rd, etc.)
    private String getOrdinalSuffix(int place) {
        if (place % 100 >= 11 && place % 100 <= 13) {
            return place + "th";
        }
        switch (place % 10) {
            case 1: return place + "st";
            case 2: return place + "nd";
            case 3: return place + "rd";
            default: return place + "th";
        }
    }
}
