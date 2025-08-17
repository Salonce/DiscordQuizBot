package dev.salonce.discordquizbot.infrastructure.messages.out;
import dev.salonce.discordquizbot.domain.Match;
import dev.salonce.discordquizbot.application.MatchService;
import discord4j.core.spec.EmbedCreateSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static dev.salonce.discordquizbot.util.DiscordFormatter.formatMentions;

@RequiredArgsConstructor
@Component
public class MatchResultsMessage {

    private final MatchService matchService;

    public EmbedCreateSpec createEmbed(Match match){
        return EmbedCreateSpec.builder()
                .title("\uD83C\uDFC6 Final scoreboard")
                .addField("\uD83D\uDCD8 Subject: " + match.getTitle() + " " + match.getDifficulty(), "", false)
                .addField("❓ Questions: " + match.getQuestions().size(), "", false)
                .addField("", getFinalScoreboard(match), false)
                .build();
    }

    private String getFinalScoreboard(Match match) {
        Map<Integer, List<Long>> pointsGrouped = match.getPlayersGroupedByPoints();

        // Sort the points in descending order
        List<Integer> sortedPoints = pointsGrouped.keySet().stream()
                .sorted((a, b) -> b - a)
                .toList();

        // Build the scoreboard message
        StringBuilder scoreboard = new StringBuilder();
        int place = 1;

        for (Integer points : sortedPoints) {
            List<Long> playerIds = pointsGrouped.get(points);
            String playersList = formatMentions(playerIds).replace("<@", "**<@").replace(">", ">**");
            String pointWord = points == 1 ? "point" : "points";

            String label = switch (place) {
                case 1 -> "🥇";
                case 2 -> "🥈";
                case 3 -> "🥉";
                default -> getOrdinalSuffix(place);
            };

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
        return switch (place % 10) {
            case 1 -> place + "st";
            case 2 -> place + "nd";
            case 3 -> place + "rd";
            default -> place + "th";
        };
    }
}
