package dev.salonce.discordQuizBot.Core.MessagesSending;

import dev.salonce.discordQuizBot.Core.MatchStore;
import dev.salonce.discordQuizBot.Core.Matches.MatchState;
import dev.salonce.discordQuizBot.Core.Matches.Match;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class MatchCanceledMessage {

    private final MatchStore matchStore;

    public Mono<Message> create(MessageChannel messageChannel){
        Match match = matchStore.get(messageChannel);
        String title = "\uD83D\uDEAA Match aborted.";
        String reason = "unknown.";
        if (match.getMatchState() == MatchState.CLOSED_BY_INACTIVITY)
            reason = "autoclosed due to player's inactivity.";
        else if (match.getMatchState() == MatchState.CLOSED_BY_OWNER)
            reason = "the owner <@" + match.getOwnerId() + ">" + " aborted the match.";

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title(title)
                .addField("\uD83D\uDCD8 Subject: " + match.getTopic() + " " + match.getDifficulty(), "", false)
                .addField("❓ Questions: " + match.getQuestions().size(), "", false)
                .addField("" , "**\uD83E\uDD14 Reason: " + "**", false)
                .build();

        return messageChannel.createMessage(embed);
    }
}
