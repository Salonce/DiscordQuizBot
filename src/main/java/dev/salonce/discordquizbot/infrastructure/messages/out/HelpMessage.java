package dev.salonce.discordquizbot.infrastructure.messages.out;

import dev.salonce.discordquizbot.application.RawQuestionsService;
import dev.salonce.discordquizbot.domain.Topic;
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

    private final RawQuestionsService rawQuestionsService;

    public EmbedCreateSpec createEmbed() {
        Map<String, Topic> topics = rawQuestionsService.getTopicsMap();

        if (topics.isEmpty()) {
            return createNoDataEmbed();
        }

        return createQuizHelpEmbed(topics);
    }

    private EmbedCreateSpec createNoDataEmbed() {
        return EmbedCreateSpec.builder()
                .title("No data")
                .addField("", "Sorry. This bot has no available quizzes.", false)
                .build();
    }

    private EmbedCreateSpec createQuizHelpEmbed(Map<String, Topic> topics) {
        String examples = createExamples(topics);
        String categories = createCategoriesList(topics);

        return EmbedCreateSpec.builder()
                .addField("Basics", "Choose a category. Start at level 1. Each level adds 50 questions. Move up in levels when you can easily score 9-10/10.", false)
                .addField("How to start a quiz?", "Choose a category, its level and type. Template:\n **qq quiz <category> <difficulty level>**", false)
                .addField("Examples", examples, false)
                .addField("Categories (levels)", categories, false)
                .build();
    }

    private String createExamples(Map<String, Topic> topics) {
        Iterator<Topic> iterator = topics.values().iterator();
        StringBuilder examples = new StringBuilder();

        if (iterator.hasNext()) {
            Topic topic1 = iterator.next();
            examples.append(createExampleText(topic1.getName(), 1));
        }

        if (iterator.hasNext()) {
            Topic topic2 = iterator.next();
            examples.append(createExampleText(topic2.getName(), 2));
        }

        return examples.toString();
    }

    private String createExampleText(String topicName, int difficulty) {
        return "To start **" + topicName + "** quiz, at level " + difficulty +
                ", type: **qq quiz " + topicName + " " + difficulty + "**\n";
    }

    private String createCategoriesList(Map<String, Topic> topics) {
        return topics.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(this::formatCategoryEntry)
                .collect(Collectors.joining("\n"));
    }

    private String formatCategoryEntry(Map.Entry<String, Topic> entry) {
        String topic = entry.getKey();
        int maxDifficulty = entry.getValue().getDifficulties().size();
        return topic + " (1-" + maxDifficulty + ")";
    }

}
