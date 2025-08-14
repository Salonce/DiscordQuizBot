package dev.salonce.discordquizbot.infrastructure.configs;

import dev.salonce.discordquizbot.infrastructure.buttons.ButtonHandler;
import dev.salonce.discordquizbot.infrastructure.buttons.ButtonHandlerChain;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class ButtonHandlerChainConfig {
    @Bean
    public ButtonHandlerChain getButtonHandlerChain(
            @Qualifier("ButtonCancelMatch") ButtonHandler buttonCancelMatch,
            @Qualifier("ButtonAnswer") ButtonHandler buttonAnswer,
            @Qualifier("ButtonFallback") ButtonHandler buttonFallBack,
            @Qualifier("ButtonJoinMatch") ButtonHandler buttonJoinMatch,
            @Qualifier("ButtonLeaveMatch") ButtonHandler buttonLeaveMatch,
            @Qualifier("ButtonStartNow") ButtonHandler buttonStartNow) {
        return new ButtonHandlerChain(Arrays.asList(buttonCancelMatch, buttonAnswer, buttonJoinMatch, buttonLeaveMatch, buttonStartNow,  buttonFallBack));
    }
}
