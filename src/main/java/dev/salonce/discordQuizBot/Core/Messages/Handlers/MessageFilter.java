package dev.salonce.discordQuizBot.Core.Messages.Handlers;

import dev.salonce.discordQuizBot.Core.Messages.DiscordMessage;
import dev.salonce.discordQuizBot.Core.Messages.MessageHandler;
import dev.salonce.discordQuizBot.Core.Messages.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static java.lang.Thread.sleep;

@Component("messageFilter")
@RequiredArgsConstructor
public class MessageFilter implements MessageHandler {
    private final MessageSender messageSender;

    @Override
    public boolean handleMessage(DiscordMessage discordMessage){
        if (discordMessage.getContent() == null){
            return true;
        }
        else if (discordMessage.getContent().equalsIgnoreCase("qq")) {

            messageSender.sendMessage(discordMessage, "Empty request").subscribe();
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
