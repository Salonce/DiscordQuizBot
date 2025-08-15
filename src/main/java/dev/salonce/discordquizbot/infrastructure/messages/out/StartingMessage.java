package dev.salonce.discordquizbot.infrastructure.messages.out;

import dev.salonce.discordquizbot.domain.Match;
import dev.salonce.discordquizbot.application.MatchService;
import dev.salonce.discordquizbot.domain.Player;
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

    private final MatchService matchService;

    public MessageCreateSpec createSpec(Match match, int timeToJoinLeft){
        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("\uD83D\uDE80 Starting Soon...")
                .addField("\uD83D\uDCD8 Subject: " + match.getTopic() + " " + match.getDifficulty(), "", false)
                .addField("❓ Questions: " + match.getQuestions().size(), "", false)
                .addField("", "\uD83D\uDC65 " + "**Players:** " + getUserNames(match), false)
                .addField("", "```⏳ " + timeToJoinLeft + " seconds to start.``` ", false)
                .build();

        return MessageCreateSpec.builder()
                .addComponent(ActionRow.of(Button.primary("startNow", "Start now"), Button.success("joinQuiz", "Join"), Button.success("leaveQuiz", "Leave"), Button.danger("cancelQuiz", "Cancel")))
                .addEmbed(embed)
                .build();
    }

    public MessageEditSpec editSpec(Match match, Long timeToJoinLeft){
        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("\uD83D\uDE80 Starting Soon...")
                .addField("\uD83D\uDCD8 Subject: " + match.getTopic() + " " + match.getDifficulty(), "", false)
                .addField("❓ Questions: " + match.getQuestions().size(), "", false)
                .addField("", "\uD83D\uDC65 " + "**Players:** " + getUserNames(match), false)
                .addField("", "```⏳ " + timeToJoinLeft + " seconds to start.``` ", false)
                .build();

        return MessageEditSpec.builder()
                .addComponent(ActionRow.of(Button.primary("startNow", "Start now"), Button.success("joinQuiz", "Join"), Button.success("leaveQuiz", "Leave"), Button.danger("cancelQuiz", "Cancel")))
                .addEmbed(embed)
                .build();
    }

    public MessageEditSpec editSpec2(Match match, Long timeToStartLeft){
        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("\uD83D\uDE80 Starting Soon...")
                .addField("\uD83D\uDCD8 Subject: " + match.getTopic() + " " + match.getDifficulty(), "", false)
                .addField("❓ Questions: " + match.getQuestions().size(), "", false)
                .addField("", "\uD83D\uDC65 " + "**Players:** " + getUserNames(match), false)
                .addField("", "```⏳ " + timeToStartLeft + " seconds to start.``` ", false)
                .build();

        return MessageEditSpec.builder()
                .addComponent(ActionRow.of(Button.primary("startNow", "Start now").disabled(), Button.success("joinQuiz", "Join").disabled(), Button.success("leaveQuiz", "Leave").disabled(), Button.danger("cancelQuiz", "Cancel").disabled()))
                .addEmbed(embed)
                .build();
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
