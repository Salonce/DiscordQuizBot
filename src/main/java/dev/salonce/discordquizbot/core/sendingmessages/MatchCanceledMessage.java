package dev.salonce.discordquizbot.core.sendingmessages;

import dev.salonce.discordquizbot.application.MatchService;
import dev.salonce.discordquizbot.domain.MatchState;
import dev.salonce.discordquizbot.domain.Match;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class MatchCanceledMessage {

    private final MatchService matchService;

    public Mono<Message> create(MessageChannel messageChannel){
        Match match = matchService.get(messageChannel);
        String title = "\uD83D\uDEAA Match aborted";
        String reason = "unknown.";
        if (match.getMatchState() == MatchState.CLOSED_BY_INACTIVITY)
            reason = "autoclosed due to players' inactivity.";
        else if (match.getMatchState() == MatchState.CLOSED_BY_OWNER)
            reason = "<@" + match.getOwnerId() + "> (owner)" + " has cancelled the match.";

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title(title)
                .addField("\uD83D\uDCD8 Subject: " + match.getTopic() + " " + match.getDifficulty(), "", false)
                .addField("❓ Questions: " + match.getQuestions().size(), "", false)
                .addField("" , "**\uD83E\uDD14 Reason: " + reason + "**", false)
                .build();

        return messageChannel.createMessage(embed);
    }
}
