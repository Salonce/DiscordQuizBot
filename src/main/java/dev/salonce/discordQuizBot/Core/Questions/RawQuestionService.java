package dev.salonce.discordQuizBot.Core.Questions;

import dev.salonce.discordQuizBot.Configs.AvailableTopicsConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class RawQuestionService {

    private final RawQuestionRepository rawQuestionRepository;
    private final AvailableTopicsConfig availableTopicsConfig;

    private final Map<String, List<List<RawQuestion>>> topicRawQuestionSets = new HashMap<>();


    private List<RawQuestion> generateRawQuestions(String topic){
        List<RawQuestion> rawQuestions = new ArrayList<>();
        for (RawQuestion rawQuestion : rawQuestionRepository.getRawQuestions()){
            if (rawQuestion.containsTag(topic))
                rawQuestions.add(rawQuestion);
        }
        return rawQuestions;
    }

    public static void sortQuestions(List<RawQuestion> questions) {
        questions.sort(Comparator
                .comparing(RawQuestion::getDifficulty, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(RawQuestion::getQuestion, Comparator.nullsLast(String::compareToIgnoreCase)));
    }

    @PostConstruct
    public void loadTopicRawQuestionSets(){
        for (String topic :  availableTopicsConfig.getAvailableTopics().keySet()){
            //generate
            List<RawQuestion> rawTopicQuestions = generateRawQuestions(topic);

            //make difficultyList for each topic
            List<List<RawQuestion>> topicDifficultyList = new ArrayList<>();
            topicRawQuestionSets.put(topic, topicDifficultyList);

            // sort by difficulty, if not then question string
            sortQuestions(rawTopicQuestions);
            // add 50~ unique question to each level
            // remove the added ones from list on the fly
            // have lists with separate question levels
            int difficulty = 0;
            while (rawTopicQuestions.size() >= 50){
                List<RawQuestion> rawQuestions = new ArrayList<>();
                for (int i = 0; i < 50; i++){
                    rawQuestions.add(rawTopicQuestions.get(0));
                    rawTopicQuestions.remove(0);
                }
                topicDifficultyList.add(rawQuestions);
                difficulty++;
            }
            // first just separately add level sets to the game
            // later use the lists to generate random sets with % contribution

        }
    }

    public boolean doesQuestionSetExist(String topic, int difficulty){
        if (!topicRawQuestionSets.containsKey(topic))
            return false;
        if (!(topicRawQuestionSets.get(topic).size() < difficulty))
            return false;
        return true;
    }

    public List<RawQuestion> getRawQuestionSet(String topic, int difficulty){
        if (!doesQuestionSetExist(topic, difficulty))
            return null;
        List<RawQuestion> rawQuestions = new ArrayList<>();
        return new ArrayList<>(topicRawQuestionSets.get(topic).get(difficulty));
    }


    public List<RawQuestion> getRawQuestions(String tag, int difficulty){
        List<RawQuestion> rawQuestionSubset = new ArrayList<>();
        for (RawQuestion rawQuestion : rawQuestionRepository.getRawQuestions())
            if (rawQuestion.getTags().contains(tag) && rawQuestion.getDifficulty() <= difficulty)
                rawQuestionSubset.add(rawQuestion);
        return rawQuestionSubset;
    }
}
