package dev.salonce.discordquizbot.infrastructure.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogExecutionTime {

    @Pointcut("execution(public * dev.salonce.discordquizbot.core.questions.rawquestions.RawQuestionLoader.loadQuestionsFromResources(..))")
    public void loadQuestionsFromResources(){};

    @Around("loadQuestionsFromResources()")
    public Object logLoadingQuestionsTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startingTime = System.currentTimeMillis();
        Object object = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startingTime;
        System.out.println("Loading raw JSON questions from resources took " + executionTime + " miliseconds.");
        return object;
    }
}
