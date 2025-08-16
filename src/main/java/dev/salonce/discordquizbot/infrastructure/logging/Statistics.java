package dev.salonce.discordquizbot.infrastructure.logging;

import dev.salonce.discordquizbot.domain.Match;
import discord4j.core.GatewayDiscordClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class Statistics {

    private final GatewayDiscordClient gateway;
    private static int matchesStarted = 0;

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

    @Before("execution(* *..DiscordBotBootstrap.startBot())")
    public void logServerStats() {
        gateway.getGuilds()
                .collectList()
                .doOnNext(guilds -> log.info("Bot is in {} servers.", guilds.size()))
                .subscribe();
    }
}
