package dev.salonce.discordQuizBot.MessageHandlers.handlers;

import dev.salonce.discordQuizBot.Core.DiscordMessage;
import dev.salonce.discordQuizBot.MessageHandlers.MessageHandler;
import dev.salonce.discordQuizBot.Util.MessageSender;
import discord4j.core.object.reaction.ReactionEmoji;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static java.lang.Thread.sleep;

@Component("startQuiz")
@RequiredArgsConstructor
public class StartQuiz implements MessageHandler {
    private final MessageSender messageSender;

    @Override
    public boolean handleMessage(DiscordMessage discordMessage) {
        if (discordMessage.getContent().equalsIgnoreCase("qq quiz")) {
            messageSender.sendMessage(discordMessage, "Starting quiz. Click the door button to participate.")
                    .flatMap(message -> Flux.just("🇦", "🇧", "🇨", "🇩")
                            .flatMap(emoji -> message.addReaction(ReactionEmoji.unicode(emoji)))
                            .then(Mono.just(message)))
                    .delayElement(Duration.ofSeconds(5))  // Wait 10 seconds
                    .flatMap(message -> message.addReaction(ReactionEmoji.unicode("👍")))
                    .subscribe();
            return true;
        }
        return false;
    }
}
