package dev.salonce.discordquizbot.infrastructure.configs;

import dev.salonce.discordquizbot.application.MessageHandler;
import dev.salonce.discordquizbot.application.MessageHandlerChain;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class MessageHandlerChainConfig {
    @Bean
    public MessageHandlerChain getMessageHandlerChain(@Qualifier("messageFilter") MessageHandler messageFilter,
                                                      @Qualifier("startQuiz") MessageHandler startQuiz,
                                                      @Qualifier("help") MessageHandler help){
        return new MessageHandlerChain(Arrays.asList(messageFilter, startQuiz, help));
    }
}
