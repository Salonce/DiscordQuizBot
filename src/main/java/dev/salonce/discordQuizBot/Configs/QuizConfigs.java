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
            return new QuizConfig(4, 3, 7, 2, 5, 1);
        else if (mode.equals("standard"))
            return new QuizConfig(10, 3, 30, 3, 30, 7);
        else{
            System.out.println("ERROR! Mode " + mode + " doesn't exist! Check your application.yaml file for mode configuration.");
            return null;
        }
    }
}
