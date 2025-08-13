package dev.salonce.discordquizbot.infrastructure.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExecutionTime {

    private static final Logger logger = LoggerFactory.getLogger(ExecutionTime.class);

    @Pointcut("execution(public * dev.salonce.discordquizbot.infrastructure.RawQuestionLoader.loadQuestionsFromResources(..))")
    public void loadQuestionsFromResources(){};

    @Around("loadQuestionsFromResources()")
    public Object logLoadingQuestionsTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startingTime = System.currentTimeMillis();
        Object object = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startingTime;
        logger.info("Loading raw JSON questions from resources took {} miliseconds.", executionTime);
        return object;
    }
}
