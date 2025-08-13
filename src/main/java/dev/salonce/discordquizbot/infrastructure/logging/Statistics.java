package dev.salonce.discordquizbot.infrastructure.logging;

import dev.salonce.discordquizbot.domain.Match;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class Statistics {

    private static final Logger log = LoggerFactory.getLogger(Statistics.class);
    private int matchesStarted = 0;

    @AfterReturning(pointcut = "execution(* *..MatchService.makeMatch(..))", returning = "match")
    public void logMatchCreation(JoinPoint joinPoint, Match match) {
        matchesStarted++;
        log.info("Match nr {} started by user <{}> | Topic: '{}' | Difficulty: {}",
                matchesStarted,
                match.getOwnerId(),
                match.getTopic(),
                match.getDifficulty()
        );
    }
}
