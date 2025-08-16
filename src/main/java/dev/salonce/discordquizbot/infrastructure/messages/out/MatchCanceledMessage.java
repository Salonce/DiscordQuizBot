package dev.salonce.discordquizbot.infrastructure.messages.out;

import dev.salonce.discordquizbot.application.MatchService;
import dev.salonce.discordquizbot.domain.MatchState;
import dev.salonce.discordquizbot.domain.Match;
import discord4j.core.spec.EmbedCreateSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MatchCanceledMessage {

    public EmbedCreateSpec createEmbed(Match match){
        String title = "\uD83D\uDEAA Match aborted";
        String reason = "unknown.";
        if (match.isClosedByInactivityState())
            reason = "autoclosed due to players' inactivity.";
        else if (match.isClosedByOwnerState())
            reason = "<@" + match.getOwnerId() + "> (owner)" + " has cancelled the match.";

        return EmbedCreateSpec.builder()
                .title(title)
                .addField("\uD83D\uDCD8 Subject: " + match.getTopic() + " " + match.getDifficulty(), "", false)
                .addField("❓ Questions: " + match.getNumberOfQuestions(), "", false)
                .addField("" , "**\uD83E\uDD14 Reason: " + reason + "**", false)
                .build();
    }
}
