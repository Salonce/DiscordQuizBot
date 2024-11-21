package dev.salonce.discordQuizBot.MessageHandlers.handlers;

import dev.salonce.discordQuizBot.Core.DiscordMessage;
import dev.salonce.discordQuizBot.MessageHandlers.MessageHandler;
import dev.salonce.discordQuizBot.Util.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static java.lang.Thread.sleep;

@Component("messageFilter")
@RequiredArgsConstructor
public class MessageFilter implements MessageHandler {
    private final MessageSender messageSender;

    @Override
    public boolean handleMessage(DiscordMessage discordMessage){
        if (discordMessage.getContent().equalsIgnoreCase("qq")) {

            messageSender.sendMessage(discordMessage, "Empty request").block();
//            Mono.just(discordMessage).publishOn(Schedulers.boundedElastic())
//                            .doOnEach(message -> messageSender.sendMessage(discordMessage, "Empty request"));
//
//            //messageSender.sendMessage(discordMessage, "Empty request");
//            try{
//                sleep(3000);
//            } catch (InterruptedException e) {
//                System.out.println("Interrupted exception");
//            }
            return true;
        }
        return false;
    }
}
