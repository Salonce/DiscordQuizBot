package dev.salonce.discordQuizBot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimersConfig {

    @Bean("testingTimers")
    public Timers standardTimers(){
        return new Timers(7, 2, 5, 3);
    }

    @Bean("standardTimers")
    public Timers testingTimers(){
        return new Timers(30, 5, 35, 10);
    }
}
