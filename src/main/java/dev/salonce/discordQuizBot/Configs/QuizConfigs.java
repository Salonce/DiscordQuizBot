package dev.salonce.discordQuizBot.Configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuizConfigs {

    @Value("${mode}")
    private String mode;

    @Bean

    public QuizConfig getTimers(){
        if (mode.equals("testing"))
            return new QuizConfig(5, 10, 7, 2, 5, 3);
        else if (mode.equals("testing2"))
            return new QuizConfig(5, 2, 5, 5, 30, 3);
        else if (mode.equals("standard"))
            return new QuizConfig(10, 2, 30, 5, 30, 10);
        else if (mode.equals("eureka"))
            return new QuizConfig(10, 2, 15, 2, 15, 7);
        else{
            System.out.println("ERROR! Mode " + mode + " doesn't exist! Check your application.yaml file for mode configuration.");
            return null;
        }
    }
}
