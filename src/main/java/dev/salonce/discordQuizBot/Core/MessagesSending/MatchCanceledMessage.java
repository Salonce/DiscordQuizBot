package dev.salonce.discordQuizBot.Core.MessagesSending;

import dev.salonce.discordQuizBot.Core.MatchStore;
import dev.salonce.discordQuizBot.Core.Matches.EnumMatchClosed;
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
        String text = "Match has been closed.";
        if (match.getEnumMatchClosed() == EnumMatchClosed.BY_AUTOCLOSE)
            text = "Match has been autoclosed.";
        else if (match.getEnumMatchClosed() == EnumMatchClosed.BY_OWNER)
            text = "Match has been closed by the owner.";

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title(text)
                .build();

        return messageChannel.createMessage(embed);
    }
}
