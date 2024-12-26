package dev.salonce.discordQuizBot.Core.Questions;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.salonce.discordQuizBot.Configs.QuestionSetsConfig;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

@Component
public class QuestionRepository {

    private final Map<String, List<RawQuestion>> questionMap = new HashMap<>();

//    public QuestionRepository() throws IOException {
//        questionMap.put("java", loadQuestionsFromFile("src/main/resources/java.json"));
//    //    questionMap.put("docker", loadQuestionsFromFile("src/main/resources/docker.json"));
//    }

    public QuestionRepository(QuestionSetsConfig questionSetsConfig) throws IOException {
        for (Map.Entry<String, List<String>> entry : questionSetsConfig.getFiles().entrySet()) {
            String questionType = entry.getKey();
            List<RawQuestion> allQuestionsForType = new ArrayList<>();

            for (String filePath : entry.getValue()) {
                allQuestionsForType.addAll(loadQuestionsFromFile(filePath));
                System.out.println("Loaded key: " + questionType + ". Loaded filepath: " + filePath);
            }

            questionMap.put(questionType, allQuestionsForType);
        }
    }

    private List<RawQuestion> loadQuestionsFromFile(String filePath){
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File(filePath);
            return Arrays.asList(mapper.readValue(file, RawQuestion[].class));
        }
        catch(Exception e){
            System.out.println("Couldn't load file: " + filePath);
            System.out.println("Reason: " + e.getMessage());
            System.exit(1);
            return new ArrayList<>();
        }
    }

    public List<RawQuestion> getQuestions(String type) {
        return new ArrayList<>(questionMap.getOrDefault(type, Collections.emptyList()));
    }

//    public List<RawQuestion> getDockerQuestions() {
//        return questionMap.getOrDefault("docker", Collections.emptyList());
//    }
}