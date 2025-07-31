package dev.salonce.discordquizbot.core.questions.topics;

import dev.salonce.discordquizbot.core.questions.rawquestions.RawQuestion;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
class DifficultyLevel {
    public List<RawQuestion> rawQuestions = new ArrayList<>();

    public DifficultyLevel(List<RawQuestion> rawQuestions) {
        addRawQuestions(rawQuestions);
    }

    //add questions - size for < 65 and 50 for > 65
    public void addRawQuestions(List<RawQuestion> rawQuestions) {
        if (rawQuestions.size() < 65) {
            int size = rawQuestions.size();
            for (int i = 0; i < size; i++) {
                this.rawQuestions.add(rawQuestions.get(0));
                rawQuestions.remove(0);
            }
        } else {
            for (int i = 0; i < 50; i++) {
                this.rawQuestions.add(rawQuestions.get(0));
                rawQuestions.remove(0);
            }
        }
    }
}
