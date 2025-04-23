package dev.salonce.discordQuizBot.Core.Questions;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class RawQuestionService {

    private final RawQuestionRepository rawQuestionRepository;
    private final AvailableTopicsConfig availableTopicsConfig;

    @Getter
    private final Map<String, Topic> topics = new HashMap<>();


    private List<RawQuestion> getRawQuestions(Set<String> tags){
        Set<RawQuestion> rawQuestions = new HashSet<>();
        for (RawQuestion rawQuestion : rawQuestionRepository.getRawQuestions()){
            for (String tag : tags){
                if (rawQuestion.containsTag(tag)) {
                    rawQuestions.add(rawQuestion);
                    break;
                }
            }
        }
        return new ArrayList<>(rawQuestions);
    }

    // sort by growing difficulty, if not ID
    private void sortQuestions(List<RawQuestion> questions) {
        questions.sort(Comparator
                .comparing(RawQuestion::getDifficulty, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(RawQuestion::getId, Comparator.nullsLast(Long::compareTo)));
    }

    @PostConstruct
    public void loadTopicRawQuestionSets(){

        for (Map.Entry<String, Set<String>> entry : availableTopicsConfig.getAvailableTopics().entrySet()){
            //generate
            String topic = entry.getKey();
            List<RawQuestion> rawTopicQuestions = getRawQuestions(entry.getValue());
            sortQuestions(rawTopicQuestions);
            // have lists with separate question levels
            topics.put(topic, new Topic(topic, rawTopicQuestions));
        }
    }

    public boolean doesQuestionSetExist(String topic, int level){
        if (!topics.containsKey(topic))
            return false;
        if (!topics.get(topic).difficultyLevelExists(level))
            return false;
        return true;
    }

    public List<RawQuestion> getRawQuestionList(String topic, int level){
        if (!doesQuestionSetExist(topic, level))
            return null;
        return new ArrayList<>(topics.get(topic).getDifficultyLevel(level).getRawQuestions());
    }

    //    private Set<RawQuestion> getRawQuestions(String tag){
    //        Set<RawQuestion> rawQuestions = new HashSet<>();
    //        for (RawQuestion rawQuestion : rawQuestionRepository.getRawQuestions()){
    //            if (rawQuestion.containsTag(tag))
    //                rawQuestions.add(rawQuestion);
    //        }
    //        return rawQuestions;
    //    }
}
