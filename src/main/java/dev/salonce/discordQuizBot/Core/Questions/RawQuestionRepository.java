package dev.salonce.discordQuizBot.Core.Questions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Component
public class RawQuestionRepository {

    private Set<RawQuestion> rawQuestions = new HashSet<>();

    @PostConstruct
    public void loadQuestionsFromResources() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

            // Try to load from private data folder first
            Resource[] privateResources = resolver.getResources("classpath*:private/data/**/*.json");

            // If private resources exist, use them
            Resource[] resources = privateResources.length > 0 ?
                    privateResources :
                    resolver.getResources("classpath*:sample/data/**/*.json");

            String sourcePath = privateResources.length > 0 ? "private/data" : "sample/data";
            System.out.println("📂 Loading questions from: " + sourcePath);

            for (Resource resource : resources) {
                String path = resource.getURI().toString();
                System.out.println("📂 Found file: " + path);

                try (InputStream is = resource.getInputStream()) {
                    CollectionType listType = objectMapper.getTypeFactory()
                            .constructCollectionType(List.class, RawQuestion.class);
                    List<RawQuestion> loaded = objectMapper.readValue(is, listType);
                    System.out.println("Loaded " + loaded.size() + " from file " + path);
                    rawQuestions.addAll(loaded);
                } catch (IOException e) {
                    System.err.println("❌ Failed to load file: " + path + " → " + e.getMessage());
                }
            }
            System.out.println("✅ Total questions loaded: " + rawQuestions.size());
        } catch (IOException e) {
            System.err.println("❌ Error scanning for JSON files: " + e.getMessage());
        }
    }
}