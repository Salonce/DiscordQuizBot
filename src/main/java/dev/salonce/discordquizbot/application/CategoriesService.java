package dev.salonce.discordquizbot.application;

import dev.salonce.discordquizbot.domain.Categories;
import dev.salonce.discordquizbot.domain.Category;
import dev.salonce.discordquizbot.domain.DifficultyLevel;
import dev.salonce.discordquizbot.infrastructure.configs.TopicsConfig;
import dev.salonce.discordquizbot.infrastructure.dtos.RawQuestion;
import dev.salonce.discordquizbot.infrastructure.storage.RawQuestionStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CategoriesService {

    private final RawQuestionsService rawQuestionsService;
    private final TopicsConfig topicsConfig;
    private final RawQuestionStore rawQuestionStore;

    private final Categories categories = new Categories();

    public CategoriesService(RawQuestionsService rawQuestionsService, TopicsConfig topicsConfig, RawQuestionStore rawQuestionStore){
        this.rawQuestionsService = rawQuestionsService;
        this.topicsConfig = topicsConfig;
        this.rawQuestionStore = rawQuestionStore;

        for (Map.Entry<String, Set<String>> entry : topicsConfig.getAvailableTopics().entrySet()) {
            String topicName = entry.getKey();
            Set<String> tagsSet = entry.getValue();
            List<RawQuestion> rawTopicQuestions = rawQuestionsService.getRawQuestionsForTags(tagsSet);
            List<DifficultyLevel> difficultyLevels = rawQuestionsService.prepareDifficultyLevels(rawTopicQuestions);
            Category category = new Category(topicName, difficultyLevels);
            categories.addTopic(topicName, category);
        }
    }

    public Categories getCategories() {
        return categories;
    }

    public boolean areNoCategoriesAvailable(){
        return categories.areNone();
    }

    public boolean doesCategoryExist(String topic, int level){
        return categories.doesQuestionSetExist(topic, level);
    }

    public List<RawQuestion> getRawQuestionList(String topic, int level){
        return categories.getRawQuestionList(topic, level);
    }


}
