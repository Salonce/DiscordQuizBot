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
        String example = null; Integer exampleDifficulty = -1;
        String example2 = null; Integer exampleDifficulty2 = -1;
        Iterator<Map.Entry<String, List<List<RawQuestion>>>> iterator = topicRawQuestionSets.entrySet().iterator();
        String firstExample = "";
        if (iterator.hasNext()) {
            Map.Entry<String, List<List<RawQuestion>>> mapEntry1 = iterator.next();
            example = mapEntry1.getKey();
            exampleDifficulty = mapEntry1.getValue().size();
            firstExample = "To start **" + example + "** quiz, type: **qq quiz " + example + " " + exampleDifficulty + "**\n";
        }
        String secondExample = "";
        if (iterator.hasNext()) {
            Map.Entry<String, List<List<RawQuestion>>> mapEntry2 = iterator.next();
            example2 = mapEntry2.getKey();
            exampleDifficulty2 = mapEntry2.getValue().size();
            secondExample = "To start **" + example2 + "** quiz, type: **qq quiz " + example2 + " " + exampleDifficulty2 + "**\n";
        }

        EmbedCreateSpec embed;
        //categories list
        //switch to if fail then dont do and move embed
        if (example != null && exampleDifficulty != null) {
            EmbedCreateSpec.Builder embedBuilder = EmbedCreateSpec.builder();

            String categories = availableTopicsConfig.getAvailableTopics().entrySet().stream()
                    .map(entry -> {
                        String topic = entry.getKey();
                        String difficulties = entry.getValue().stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining(", "));
                        return topic + " (" + difficulties + ")";
                    })
                    .collect(Collectors.joining("\n"));


            embed = embedBuilder
                    .addField("How to start a quiz?", "Choose a category and type: **qq quiz <selected category> <selected difficulty level>**", false)
                    .addField("Examples", firstExample + secondExample, false)
                    .addField("Available categories", categories, false)
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
