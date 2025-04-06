package dev.salonce.discordQuizBot.Core.MessagesSending;

import dev.salonce.discordQuizBot.Configs.QuestionSetsConfig;
import dev.salonce.discordQuizBot.Core.MatchStore;
import dev.salonce.discordQuizBot.Core.Matches.Match;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HelpMessage {

    private final QuestionSetsConfig questionSetsConfig;
    private final MatchStore matchStore;


    public Mono<Message> create(MessageChannel messageChannel) {
        Match match = matchStore.get(messageChannel);
        String example = null;
        String example2 = null;
        Iterator<String> iterator = questionSetsConfig.getFiles().keySet().iterator();
        if (iterator.hasNext())
            example = iterator.next();
        if (iterator.hasNext())
            example2 = iterator.next();

        EmbedCreateSpec embed;
        if (example != null && example2 != null) {
            EmbedCreateSpec.Builder embedBuilder = EmbedCreateSpec.builder();

            List<String> categories = questionSetsConfig.getFiles().keySet().stream().sorted(String::compareTo).toList();

            embed = embedBuilder
                    .addField("How to start a quiz?", "Choose a category and type: **qq quiz <selected category>**", false)
                    .addField("Examples", "To start **" + example + "** quiz, type: **qq quiz " + example + "**\n" + "To start **" + example2 + "** quiz, type: **qq quiz " + example2 + "**", false)
                    .addField("Available categories", categories.stream().collect(Collectors.joining("\n")), false)
                    .build();
        }
        else{
            embed = EmbedCreateSpec.builder()
                    .title("No data" )
                    .addField("", "Sorry. This bot has no available quizzes.", false)
                    .build();
        }

        return messageChannel.createMessage(embed);
    }

}
