package dev.salonce.discordQuizBot.Core.MessagesSending;

import dev.salonce.discordQuizBot.Configs.AvailableTopicsConfig;
import dev.salonce.discordQuizBot.Core.Questions.RawQuestion;
import dev.salonce.discordQuizBot.Core.Questions.RawQuestionRepository;
import dev.salonce.discordQuizBot.Core.MatchStore;
import dev.salonce.discordQuizBot.Core.Matches.Match;
import dev.salonce.discordQuizBot.Core.Questions.RawQuestionService;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HelpMessage {

    private final RawQuestionService rawQuestionService;
    private final MatchStore matchStore;
    private final AvailableTopicsConfig availableTopicsConfig;

    //examples
    public Mono<Message> create(MessageChannel messageChannel) {
        Map<String, List<List<RawQuestion>>> topicRawQuestionSets = rawQuestionService.getTopicRawQuestionSets();
        String example = null;
        Integer exampleDifficulty = -1;
        String example2 = null;
        Integer exampleDifficulty2 = -1;
        Iterator<Map.Entry<String, List<List<RawQuestion>>>> iterator = topicRawQuestionSets.entrySet().iterator();
        String firstExample = "";
        if (iterator.hasNext()) {
            Map.Entry<String, List<List<RawQuestion>>> mapEntry1 = iterator.next();
            example = mapEntry1.getKey();
            exampleDifficulty = mapEntry1.getValue().size();
            firstExample = "To start **" + example + "** quiz, at level " + exampleDifficulty + ", type: **qq quiz " + example + " " + exampleDifficulty + "**\n";
        }
        String secondExample = "";
        if (iterator.hasNext()) {
            Map.Entry<String, List<List<RawQuestion>>> mapEntry2 = iterator.next();
            example2 = mapEntry2.getKey();
            exampleDifficulty2 = mapEntry2.getValue().size();
            secondExample = "To start **" + example2 + "** quiz, at level " + exampleDifficulty2 + ", type: **qq quiz " + example2 + " " + exampleDifficulty2 + "**\n";
        }

        EmbedCreateSpec embed;
        //categories list
        //switch to if fail then dont do and move embed
        if (example != null && exampleDifficulty != null) {
            EmbedCreateSpec.Builder embedBuilder = EmbedCreateSpec.builder();

            String categories = rawQuestionService.getTopicRawQuestionSets().entrySet().stream().map(entry -> {
                        String topic = entry.getKey();
                        int maxDifficulty = entry.getValue().size();
                        return topic + " (1-" + maxDifficulty + ")";
                    })
                    .collect(Collectors.joining("\n"));

            embed = embedBuilder
                    .addField("Basics", "Quizzes are separated by categories and levels. Each category has its own leveling system. It's advised to start at beginner levels and increase the difficulty when you get very comfortable at previous levels (scoring 8-10). Higher levels include questions from previous levels, so it will be hard to pass them without finishing previous levels. Spaced repetition is included is included while moving up in levels.", false)
                    .addField("How to start a quiz?", "Choose a category, its level and type: **qq quiz <selected category> <selected difficulty level>**", false)
                    .addField("Examples", firstExample + secondExample, false)
                    .addField("Available categories (levels)", categories, false)
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
