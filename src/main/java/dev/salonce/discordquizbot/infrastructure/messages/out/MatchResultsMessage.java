package dev.salonce.discordquizbot.infrastructure.messages.out;
import dev.salonce.discordquizbot.domain.Match;
import dev.salonce.discordquizbot.application.MatchService;
import dev.salonce.discordquizbot.domain.RankGroup;
import discord4j.core.spec.EmbedCreateSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static dev.salonce.discordquizbot.infrastructure.util.DiscordFormatter.formatMentions;

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
        List<RankGroup> rankGroups = match.getScoreboard().getRankGroups();

        return rankGroups.stream()
                .map(this::formatRankGroup)
                .collect(Collectors.joining("\n"));
    }

    private String formatRankGroup(RankGroup rankGroup) {
        String playersList = formatMentions(rankGroup.getPlayerIds())
                .replace("<@", "**<@")
                .replace(">", ">**");

        String pointWord = rankGroup.getPoints() == 1 ? "point" : "points";
        String label = getRankLabel(rankGroup.getRank());

        return label + ": " + playersList + " — **" + rankGroup.getPoints() + " " + pointWord + "**";
    }

    private String getRankLabel(int rank) {
        return switch (rank) {
            case 1 -> "🥇";
            case 2 -> "🥈";
            case 3 -> "🥉";
            default -> toOrdinalString(rank);
        };
    }

    // Helper method to get the ordinal suffix (1st, 2nd, 3rd, etc.)
    private String toOrdinalString(int place) {
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
