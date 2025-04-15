package dev.salonce.discordQuizBot.Core.Questions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@Component
public class RawQuestionRepository {

    private Set<RawQuestion> rawQuestions = new HashSet<>();

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void loadQuestionsFromResources() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath*:sample/data/**/*.json");

            for (Resource resource : resources) {
                String path = resource.getURI().toString();
                System.out.println("📂 Found file: " + path);

                try (InputStream is = resource.getInputStream()) {
                    CollectionType listType = objectMapper.getTypeFactory()
                            .constructCollectionType(List.class, RawQuestion.class);
                    List<RawQuestion> loaded = objectMapper.readValue(is, listType);
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