package dev.salonce.discordQuizBot.Core.Questions;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class RawQuestionRepository {

    private final RawQuestionLoader rawQuestionLoader;
    private final TopicsConfig topicsConfig;
    private List<RawQuestion> rawQuestions;

    @Getter
    private final Map<String, Topic> topicsMap = new HashMap<>();

    @PostConstruct
    public void init(){
        rawQuestions = rawQuestionLoader.loadQuestionsFromResources();

        for (Map.Entry<String, Set<String>> entry : topicsConfig.getAvailableTopics().entrySet()){
            String topicName = entry.getKey();
            Set<String> tagsSet = entry.getValue();
            List<RawQuestion> rawTopicQuestions = getRawQuestions(tagsSet);
            topicsMap.put(topicName, new Topic(topicName, rawTopicQuestions));
        }
    }

    private List<RawQuestion> getRawQuestions(Set<String> tags){
        Set<RawQuestion> rawQuestions = new HashSet<>();
        for (RawQuestion rawQuestion : this.rawQuestions){
            for (String tag : tags){
                if (rawQuestion.containsTag(tag)) {
                    rawQuestions.add(rawQuestion);
                    break;
                }
            }
        }
        return new ArrayList<>(rawQuestions);
    }


    public boolean doesQuestionSetExist(String topic, int level){
        if (!topicsMap.containsKey(topic))
            return false;
        if (!topicsMap.get(topic).difficultyLevelExists(level))
            return false;
        return true;
    }

    public List<RawQuestion> getRawQuestionList(String topic, int level){
        if (!doesQuestionSetExist(topic, level))
            return null;
        return new ArrayList<>(topicsMap.get(topic).getDifficultyLevel(level).getRawQuestions());
    }
}
