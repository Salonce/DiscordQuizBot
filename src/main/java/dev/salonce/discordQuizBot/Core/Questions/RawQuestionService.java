package dev.salonce.discordQuizBot.Core.Questions;

import dev.salonce.discordQuizBot.Configs.AvailableTopicsConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RawQuestionService {

    private final RawQuestionRepository rawQuestionRepository;
    private final AvailableTopicsConfig availableTopicsConfig;

    private final HashMap<String, List<Set<RawQuestion>>> topicRawQuestionSets;

    @PostConstruct
    public void loadTopicRawQuestionSets(){

    }

    public boolean doesQuestionSetExist(String topic, int difficulty){
        if (!topicRawQuestionSets.containsKey(topic))
            return false;
        if (!(topicRawQuestionSets.get(topic).size() < difficulty))
            return false;
        return true;
    }


    public List<RawQuestion> getRawQuestions(String tag, int difficulty){
        List<RawQuestion> rawQuestionSubset = new ArrayList<>();
        for (RawQuestion rawQuestion : rawQuestionRepository.getRawQuestions())
            if (rawQuestion.getTags().contains(tag) && rawQuestion.getDifficulty() <= difficulty)
                rawQuestionSubset.add(rawQuestion);
        return rawQuestionSubset;
    }
}
