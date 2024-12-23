package dev.salonce.discordQuizBot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimersConfig {

    @Value("${mode}")
    private String mode;

    @Bean

    public Timers getTimers(){
        if (mode.equals("testing"))
            return new Timers(7, 2, 5, 3);
        else if (mode.equals("testing2"))
            return new Timers(5, 5, 30, 3);
        else if (mode.equals("standard"))
            return new Timers(30, 5, 30, 10);
        else if (mode.equals("eureka"))
            return new Timers(15, 5, 15, 7);
        else{
            System.out.println("ERROR! Mode " + mode + " doesn't exist! Check your application.yaml file for mode configuration.");
            return null;
        }
    }
}
