package dev.salonce.discordQuizBot.Core.MessagesSending;

import dev.salonce.discordQuizBot.Core.MatchStore;
import dev.salonce.discordQuizBot.Core.Matches.Match;
import dev.salonce.discordQuizBot.Core.Matches.Player;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Iterator;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StartingMessage {

    private final MatchStore matchStore;

    public Mono<Message> create(MessageChannel messageChannel, int timeToJoinLeft){
        Match match = matchStore.get(messageChannel);

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("Quiz")
                .addField("Subject: " + match.getName(), "", false)
                .addField("Questions: " + match.getQuestions().size(), "", false)
                .addField("", "\uD83D\uDC65 " + "**Players:** " + getUserNames(match), false)
                .addField("", "```" + timeToJoinLeft + " seconds to start.``` ", false)
                .build();

        MessageCreateSpec spec = MessageCreateSpec.builder()
                .addComponent(ActionRow.of(Button.primary("startNow", "Start now"), Button.success("joinQuiz", "Join"), Button.success("leaveQuiz", "Leave"), Button.danger("cancelQuiz", "Cancel")))
                .addEmbed(embed)
                .build();

        return messageChannel.createMessage(spec);
    }

    public Mono<Message> edit(Message message, MessageChannel messageChannel, Long timeToJoinLeft){
        Match match = matchStore.get(messageChannel);

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                //.title("\uD83C\uDFC1 Java Quiz")
                .title("Quiz")
                .addField("Subject: " + match.getName(), "", false)
                .addField("Questions: " + match.getQuestions().size(), "", false)
                .addField("", "\uD83D\uDC65 " + "**Players:** " + getUserNames(match), false)
                .addField("", "```" + timeToJoinLeft + " seconds to start.``` ", false)
                .build();

        return message.edit(MessageEditSpec.builder()
                .addComponent(ActionRow.of(Button.primary("startNow", "Start now"), Button.success("joinQuiz", "Join"), Button.success("leaveQuiz", "Leave"), Button.danger("cancelQuiz", "Cancel")))
                .addEmbed(embed)
                .build());
    }

    public Mono<Message> edit2(Message message, MessageChannel messageChannel, Long timeToStartLeft){
        Match match = matchStore.get(messageChannel);

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                //.title("\uD83C\uDFC1 Java Quiz")
                .title("Quiz")
                .addField("Subject: " + match.getName(), "", false)
                .addField("Questions: " + match.getQuestions().size(), "", false)
                .addField("", "\uD83D\uDC65 " + "**Players:** " + getUserNames(match), false)
                .addField("", "```" + timeToStartLeft + " seconds to start.``` ", false)
                .build();

        return message.edit(MessageEditSpec.builder()
                .addComponent(ActionRow.of(Button.primary("startNow", "Start now").disabled(), Button.success("joinQuiz", "Join").disabled(), Button.success("leaveQuiz", "Leave").disabled(), Button.danger("cancelQuiz", "Cancel").disabled()))
                .addEmbed(embed)
                .build());
    }

    private String getUserNames(Match match) {

        Map<Long, Player> players = match.getPlayers();

        Iterator<Long> iterator = players.keySet().iterator();
        if (!iterator.hasNext())
            return "";
        Long ownerId = iterator.next();
        StringBuilder result = new StringBuilder("<@" + ownerId + "> (owner)");
        while (iterator.hasNext())
            result.append(", <@").append(iterator.next()).append(">");
        return result.toString();
    }

}
