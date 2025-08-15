package dev.salonce.discordquizbot.infrastructure.messages.out;

import dev.salonce.discordquizbot.application.MatchService;
import dev.salonce.discordquizbot.domain.MatchState;
import dev.salonce.discordquizbot.domain.Match;
import discord4j.core.object.Embed;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MatchCanceledMessage {

    private final MatchService matchService;

    public EmbedCreateSpec create(Match match){
        String title = "\uD83D\uDEAA Match aborted";
        String reason = "unknown.";
        if (match.getMatchState() == MatchState.CLOSED_BY_INACTIVITY)
            reason = "autoclosed due to players' inactivity.";
        else if (match.getMatchState() == MatchState.CLOSED_BY_OWNER)
            reason = "<@" + match.getOwnerId() + "> (owner)" + " has cancelled the match.";

        return EmbedCreateSpec.builder()
                .title(title)
                .addField("\uD83D\uDCD8 Subject: " + match.getTopic() + " " + match.getDifficulty(), "", false)
                .addField("❓ Questions: " + match.getQuestions().size(), "", false)
                .addField("" , "**\uD83E\uDD14 Reason: " + reason + "**", false)
                .build();
    }
}
