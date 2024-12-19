package dev.salonce.discordQuizBot.Core.Messages;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class MessageHandlerChainConfig {

    public MessageHandlerChain getMessageHandlerChain(@Qualifier("messageFilter") MessageHandler messageFilter, @Qualifier("javaQuiz") MessageHandler startQuiz){

        return new MessageHandlerChain(Arrays.asList(messageFilter, startQuiz));
    }
    
}
