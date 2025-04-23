package dev.salonce.discordQuizBot.Core.Questions;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Getter
class Topic{
    private List<DifficultyLevel> difficulties = new ArrayList<>();

    public Topic(List<RawQuestion> sortedRawQuestions){
        while (!sortedRawQuestions.isEmpty()){
            difficulties.add(new DifficultyLevel(sortedRawQuestions));
        }
    }

    public boolean difficultyLevelExists(int level){
        if (difficulties.size() >= level)
            return true;
        return false;
    }

    public DifficultyLevel getDifficultyLevel(int level){
        return difficulties.get(level - 1);
    }
}

@Getter
class DifficultyLevel{
    private List<RawQuestion> rawQuestions = new ArrayList<>();

    public DifficultyLevel(List<RawQuestion> rawQuestions){
        addRawQuestions(rawQuestions);
    }

    //add questions - size for < 65 and 50 for > 65
    public void addRawQuestions(List<RawQuestion> rawQuestions){
        if (rawQuestions.size() < 65){
            int size = rawQuestions.size();
            for (int i = 0; i < size; i++){
                this.rawQuestions.add(rawQuestions.get(0));
                rawQuestions.remove(0);
            }
        }
        else{
            for (int i = 0; i < 50; i++){
                this.rawQuestions.add(rawQuestions.get(0));
                rawQuestions.remove(0);
            }
        }
    }
}

@Component
@RequiredArgsConstructor
public class RawQuestionService {

    private final RawQuestionRepository rawQuestionRepository;
    private final AvailableTopicsConfig availableTopicsConfig;

    @Getter
    private final Map<String, List<List<RawQuestion>>> topics = new HashMap<>();


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

    private void makeDifficultyListForEachTopic(){
        for (String topic :  availableTopicsConfig.getAvailableTopics().keySet()){
            List<List<RawQuestion>> topicDifficultyList = new ArrayList<>();
            topics.put(topic, topicDifficultyList);
        }
    }

    @PostConstruct
    public void loadTopicRawQuestionSets(){
        makeDifficultyListForEachTopic();

        for (Map.Entry<String, Set<String>> entry : availableTopicsConfig.getAvailableTopics().entrySet()){
            //generate
            String topic = entry.getKey();
            List<RawQuestion> rawTopicQuestions = getRawQuestions(entry.getValue());
            sortQuestions(rawTopicQuestions);
            // add 50~ unique question to each level, remove the added ones from list on the fly
            // have lists with separate question levels
            fillDifficultySets(rawTopicQuestions, topic);
            // first just separately add level sets to the game
            // later use the lists to generate random sets with % contribution

        }
    }

    private void fillDifficultySets(List<RawQuestion> rawTopicQuestions, String topic){
        int difficulty = 0;
        while (rawTopicQuestions.size() >= 10){
            List<RawQuestion> rawQuestions = new ArrayList<>();
            //maybe add 60 to previous level if size < 60. that way last level will have biggest growth
            //then add next level if 10+, or make it 70/30 e.g.
            for (int i = 0; i < 50; i++){
                if (rawTopicQuestions.isEmpty()) break;
                rawQuestions.add(rawTopicQuestions.get(0));
                rawTopicQuestions.remove(0);
            }
            System.out.println("Loaded set, topic: " + topic + ", difficulty: " + difficulty);
            topics.get(topic).add(rawQuestions);
            difficulty++;
        }
    }

    public boolean doesQuestionSetExist(String topic, int difficulty){
        if (!topics.containsKey(topic))
            return false;
        if (topics.get(topic).size() < difficulty)
            return false;
        System.out.println("question set exists");
        return true;
    }

    public List<RawQuestion> getRawQuestionList(String topic, int difficulty){
        if (!doesQuestionSetExist(topic, difficulty))
            return null;
        List<RawQuestion> rawQuestions = new ArrayList<>();
        return new ArrayList<>(topics.get(topic).get(difficulty-1));
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
