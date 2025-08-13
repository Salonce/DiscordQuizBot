package dev.salonce.discordquizbot.application;

import dev.salonce.discordquizbot.domain.Match;
import dev.salonce.discordquizbot.infrastructure.configs.TimersConfig;
import dev.salonce.discordquizbot.domain.Question;
import dev.salonce.discordquizbot.infrastructure.logging.Statistics;
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
