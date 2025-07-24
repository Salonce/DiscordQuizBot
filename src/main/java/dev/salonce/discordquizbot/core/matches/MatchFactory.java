package dev.salonce.discordquizbot.core.matches;

import dev.salonce.discordquizbot.configs.TimersConfig;
import dev.salonce.discordquizbot.core.questions.Question;
import dev.salonce.discordquizbot.core.questions.QuestionListFactory;
import dev.salonce.discordquizbot.core.Stats;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class MatchFactory {

    private final TimersConfig timersConfig;
    private final QuestionListFactory questionListFactory;
    private final Stats stats;

    public Match makeMatch(String topic, int difficulty, Long ownerId){
        List<Question> questions = questionListFactory.generateMixedDifficultyQuestions(topic, difficulty, timersConfig.getNoOfQuestions());
        Match match = new Match(questions, topic, difficulty, ownerId, timersConfig.getUnansweredLimit());
        stats.addMatch(match);
        return match;
    }
}
