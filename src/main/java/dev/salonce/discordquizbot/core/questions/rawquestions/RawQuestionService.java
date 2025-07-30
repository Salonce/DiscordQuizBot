package dev.salonce.discordquizbot.core.questions.rawquestions;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RawQuestionService {

    private final RawQuestionRepository rawQuestionRepository;

    public List<RawQuestion> getRawQuestions(Set<String> tags){
        return rawQuestionRepository.getRawQuestions(tags);
    }

    public List<RawQuestion> getAllRawQuestions(){
        return rawQuestionRepository.getAllRawQuestions();
    }
}
