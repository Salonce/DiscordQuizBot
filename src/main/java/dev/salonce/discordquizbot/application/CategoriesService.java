package dev.salonce.discordquizbot.application;

import dev.salonce.discordquizbot.domain.Categories;
import dev.salonce.discordquizbot.infrastructure.dtos.RawQuestion;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public boolean doesCategoryExist(String category, int level){
        return categories.doesQuestionSetExist(category, level);
    }

    public List<RawQuestion> getRawQuestionList(String category, int level){
        return categories.getRawQuestionList(category, level);
    }


}
