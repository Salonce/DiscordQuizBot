package dev.salonce.discordQuizBot.Core.Questions;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.salonce.discordQuizBot.Configs.QuestionsConfig;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Component
public class QuestionRepository {

    private final Map<String, List<RawQuestion>> questionMap = new HashMap<>();

//    public QuestionRepository() throws IOException {
//        questionMap.put("java", loadQuestionsFromFile("src/main/resources/java.json"));
//    //    questionMap.put("docker", loadQuestionsFromFile("src/main/resources/docker.json"));
//    }

    public QuestionRepository(QuestionsConfig questionsConfig) throws IOException {
        for (Map.Entry<String, String> entry : questionsConfig.getFiles().entrySet()) {
            String questionType = entry.getKey();
            String filePath = entry.getValue();
            questionMap.put(questionType, loadQuestionsFromFile(filePath));
        }
    }

    private List<RawQuestion> loadQuestionsFromFile(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(filePath);
        return Arrays.asList(mapper.readValue(file, RawQuestion[].class));
    }

    public List<RawQuestion> getQuestions(String type) {
        return new ArrayList<>(questionMap.getOrDefault(type, Collections.emptyList()));
    }

//    public List<RawQuestion> getDockerQuestions() {
//        return questionMap.getOrDefault("docker", Collections.emptyList());
//    }
}