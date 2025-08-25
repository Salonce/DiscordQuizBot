package dev.salonce.discordquizbot.infrastructure.util;

import java.util.List;
import java.util.stream.Collectors;

public class DiscordFormatter {

    public static String formatMentions(List<Long> playerIds) {
        return playerIds.stream()
                .map(id -> "<@" + id + ">")
                .collect(Collectors.joining(", "));
    }

    public static String formatBoldMentions(List<Long> playerIds) {
        return playerIds.stream()
                .map(id -> "**<@" + id + ">**")
                .collect(Collectors.joining(", "));
    }
}