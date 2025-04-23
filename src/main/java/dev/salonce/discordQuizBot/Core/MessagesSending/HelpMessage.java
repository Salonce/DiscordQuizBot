package dev.salonce.discordQuizBot.Core.MessagesSending;

import dev.salonce.discordQuizBot.Core.Questions.TopicService;
import dev.salonce.discordQuizBot.Core.Questions.Topic;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HelpMessage {

    private final TopicService topicService;

    //examples
    public Mono<Message> create(MessageChannel messageChannel) {
        Map<String, Topic> topics = topicService.getTopicsMap();
        String example = null;
        Integer exampleDifficulty = -1;
        String example2 = null;
        Integer exampleDifficulty2 = -1;
        Iterator<Topic> iterator = topics.values().iterator();
        String firstExample = "";
        if (iterator.hasNext()) {
            Topic topic1 = iterator.next();
            example = topic1.getName();
            exampleDifficulty = 1;
            firstExample = "To start **" + example + "** quiz, at level " + exampleDifficulty + ", type: **qq quiz " + example + " " + exampleDifficulty + "**\n";
        }
        String secondExample = "";
        if (iterator.hasNext()) {
            Topic topic2 = iterator.next();
            example2 = topic2.getName();
            exampleDifficulty2 = 2;
            secondExample = "To start **" + example2 + "** quiz, at level " + exampleDifficulty2 + ", type: **qq quiz " + example2 + " " + exampleDifficulty2 + "**\n";
        }

        EmbedCreateSpec embed;
        //categories list
        //switch to if fail then dont do and move embed
        if (example != null && exampleDifficulty != null) {
            EmbedCreateSpec.Builder embedBuilder = EmbedCreateSpec.builder();

            String categories = topicService.getTopicsMap().entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(entry -> {
                        String topic = entry.getKey();
                        int maxDifficulty = entry.getValue().getDifficulties().size();
                        return topic + " (1-" + maxDifficulty + ")";
                    })
                    .collect(Collectors.joining("\n"));

            embed = embedBuilder
                    .addField("Basics", "Choose a category. Start at level 1. Each level adds 50 questions. Move up in levels when you can easily score 9-10/10.", false)
                    .addField("How to start a quiz?", "Choose a category, its level and type. Template:\n **qq quiz <category> <difficulty level>**", false)
                    .addField("Examples", firstExample + secondExample, false)
                    .addField("Categories (levels)", categories, false)
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
