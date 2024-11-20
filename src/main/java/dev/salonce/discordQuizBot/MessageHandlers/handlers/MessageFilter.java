package dev.salonce.discordQuizBot.MessageHandlers.handlers;

import dev.salonce.discordQuizBot.Core.DiscordMessage;
import dev.salonce.discordQuizBot.MessageHandlers.MessageHandler;
import dev.salonce.discordQuizBot.Util.MessageSender;
import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.lang.Thread.sleep;

@Component("messageFilter")
@RequiredArgsConstructor
public class MessageFilter implements MessageHandler {
    private final MessageSender messageSender;

    @Override
    public boolean handleMessage(DiscordMessage discordMessage){

        if (discordMessage.getContent().equalsIgnoreCase("qq")) {
            messageSender.sendMessage(discordMessage, "Empty request")
                    .flatMap(message -> Flux.just("🇦", "🇧", "🇨", "🇩")
                            .flatMap(emoji -> message.addReaction(ReactionEmoji.unicode(emoji)))
                            .then(Mono.just(message)))
                    .subscribe();
            try{
                sleep(3000);
            } catch (InterruptedException e) {
                System.out.println("Interrupted exception");
            }
            return true;
        }
        return false;
    }
}
