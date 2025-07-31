package dev.salonce.discordquizbot.core.matches;

import dev.salonce.discordquizbot.configs.TimersConfig;
import dev.salonce.discordquizbot.core.questions.questions.Question;
import dev.salonce.discordquizbot.core.statistics.Statistics;
import dev.salonce.discordquizbot.core.questions.questions.QuestionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class MatchCreationService {

    private final TimersConfig timersConfig;
    private final QuestionsService questionsService;
    private final Statistics statistics;

    Match makeMatch(String topic, int difficulty, Long ownerId){
        List<Question> questions = questionsService.generateQuestions(topic, difficulty, timersConfig.getNoOfQuestions());
        Match match = new Match(questions, topic, difficulty, ownerId, timersConfig.getUnansweredLimit());
        statistics.addMatch(match);
        return match;
    }
}
