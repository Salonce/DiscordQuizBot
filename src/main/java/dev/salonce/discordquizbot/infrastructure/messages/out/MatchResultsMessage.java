package dev.salonce.discordquizbot.infrastructure.messages.out;
import dev.salonce.discordquizbot.domain.Match;
import dev.salonce.discordquizbot.application.MatchService;
import dev.salonce.discordquizbot.domain.Player;
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

    private final MatchService matchService;

    public EmbedCreateSpec createEmbed(MessageChannel messageChannel){
        Match match = matchService.get(messageChannel);

        return EmbedCreateSpec.builder()
                .title("\uD83C\uDFC6 Final scoreboard")
                .addField("\uD83D\uDCD8 Subject: " + match.getTopic() + " " + match.getDifficulty(), "", false)
                .addField("❓ Questions: " + match.getQuestions().size(), "", false)
                .addField("", getFinalScoreboard(match), false)
                //.addField("\uD83C\uDFC6", "The winners are: " + match.getWinners(), false)
                .build();
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
            String playersList = players.stream()
                    .map(p -> "**" + p + "**")
                    .collect(Collectors.joining(", "));
            String pointWord = points == 1 ? "point" : "points";

            String label;
            switch (place) {
                case 1 -> label = "🥇";
                case 2 -> label = "🥈";
                case 3 -> label = "🥉";
                default -> label = getOrdinalSuffix(place);
            }

            scoreboard.append(label).append(": ")
                    .append(playersList).append(" — ")
                    .append("**").append(points).append(" ").append(pointWord).append("**\n");
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
