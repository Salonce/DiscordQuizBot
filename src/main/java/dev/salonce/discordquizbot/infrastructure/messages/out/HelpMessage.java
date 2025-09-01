package dev.salonce.discordquizbot.infrastructure.messages.out;

import dev.salonce.discordquizbot.application.CategoriesService;
import dev.salonce.discordquizbot.domain.Category;
import dev.salonce.discordquizbot.domain.Categories;
import discord4j.core.spec.EmbedCreateSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HelpMessage {

    private final CategoriesService categoriesService;

    public EmbedCreateSpec createEmbed() {
        if (categoriesService.areNoCategoriesAvailable()) {
            return createNoDataEmbed();
        }
        return createQuizHelpEmbed(categoriesService.getCategories());
    }

    private EmbedCreateSpec createNoDataEmbed() {
        return EmbedCreateSpec.builder()
                .title("No data")
                .addField("", "Sorry. This bot has no available quizzes.", false)
                .build();
    }

    private EmbedCreateSpec createQuizHelpEmbed(Categories topics) {
        String examples = createExamples(topics);
        String categories = createCategoriesList(topics);

        return EmbedCreateSpec.builder()
                .addField("Basics", "Choose a category. Start at level 1. Each level adds 50 questions. Move up in levels when you can easily score 9-10/10.", false)
                .addField("How to start a quiz?", "Choose a category, its level and type. Template:\n **qq quiz <category> <difficulty level>**", false)
                .addField("Examples", examples, false)
                .addField("Categories (levels)", categories, false)
                .build();
    }

    private String createExamples(Categories categories) {
        StringBuilder examples = new StringBuilder();

        //if (iterator.hasNext()) {
            Category category1 = categories.getFirstTopic();
            examples.append(createExampleText(category1.getName(), 1));
        //}

        //if (iterator.hasNext()) {
            Category category2 = categories.getSecondTopic();
            examples.append(createExampleText(category2.getName(), 2));
        //}

        return examples.toString();
    }

    private String createExampleText(String topicName, int difficulty) {
        return "To start **" + topicName + "** quiz, at level " + difficulty +
                ", type: **qq quiz " + topicName + " " + difficulty + "**\n";
    }

    private String createCategoriesList(Categories categories) {
        return categories.getSortedList().stream()
                .map(category -> category.getName() + " (1-" + category.getMaxDifficultyLevelAsInt() + ")")
                .collect(Collectors.joining("\n"));
    }
}
