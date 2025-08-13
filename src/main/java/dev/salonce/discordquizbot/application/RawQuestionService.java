package dev.salonce.discordquizbot.application;


import dev.salonce.discordquizbot.infrastructure.RawQuestionStore;
import dev.salonce.discordquizbot.infrastructure.dtos.RawQuestion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RawQuestionService {

    private final RawQuestionStore rawQuestionStore;

    public List<RawQuestion> getRawQuestions(Set<String> tags){
        return rawQuestionStore.getRawQuestions(tags);
    }

    public List<RawQuestion> getAllRawQuestions(){
        return rawQuestionStore.getAllRawQuestions();
    }
}
