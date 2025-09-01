package dev.salonce.discordquizbot.application;

import dev.salonce.discordquizbot.domain.Categories;
import dev.salonce.discordquizbot.domain.Category;
import dev.salonce.discordquizbot.domain.DifficultyLevel;
import dev.salonce.discordquizbot.infrastructure.configs.CategoriesConfig;
import dev.salonce.discordquizbot.infrastructure.dtos.RawQuestion;
import dev.salonce.discordquizbot.infrastructure.storage.RawQuestionStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CategoriesService {

    private final Categories categories;

    public CategoriesService(RawQuestionsService rawQuestionsService){
        this.categories = rawQuestionsService.createCategories();
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
