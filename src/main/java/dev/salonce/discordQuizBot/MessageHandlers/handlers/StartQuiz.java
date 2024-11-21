package dev.salonce.discordQuizBot.MessageHandlers.handlers;

import dev.salonce.discordQuizBot.Core.DiscordMessage;
import dev.salonce.discordQuizBot.Core.Match;
import dev.salonce.discordQuizBot.Core.MatchMaker;
import dev.salonce.discordQuizBot.MessageHandlers.MessageHandler;
import dev.salonce.discordQuizBot.Util.MessageSender;
import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component("startQuiz")
@RequiredArgsConstructor
public class StartQuiz implements MessageHandler {
    private final MessageSender messageSender;
    private final MatchMaker matchMaker;

    @Override
    public boolean handleMessage(DiscordMessage discordMessage) {
        if (discordMessage.getContent().equalsIgnoreCase("qq quiz")) {
            Match match;

            messageSender.sendMessage(discordMessage, "Starting quiz. Click the door button to participate.")
                    .flatMap(message -> addDoorReaction(message) // Add emojis asynchronously
                            .thenReturn(message))
//                    .delayElement(Duration.ofSeconds(5))
//                    .flatMap(message -> message.addReaction(ReactionEmoji.unicode("👍")).thenReturn(message))
//
                    .delayElement(Duration.ofSeconds(10))
                    .flatMapMany(message -> message.getReactors(ReactionEmoji.unicode("\uD83D\uDEAA")))
                    //.subscribeOn(Schedulers.boundedElastic())
                    .subscribe();
            return true;
        }

        return false;
    }

    public Mono<Void> addDoorReaction(Message message) {
        String[] emojiList = {"🇦", "🇧", "🇨", "🇩"};

        return Flux.just("\uD83D\uDEAA") // door unicode
                .concatMap(emoji -> message.addReaction(ReactionEmoji.unicode(emoji)))
                .then();
    }


//    public Mono<Void> addReactions(Message message, int number) {
//        String[] emojiList = {"🇦", "🇧", "🇨", "🇩"};
//
//        return Flux.range(0, Math.min(number, emojiList.length))
//                .map(index -> emojiList[index])
//                .concatMap(emoji -> message.addReaction(ReactionEmoji.unicode(emoji)))
//                .switchIfEmpty(Mono.fromRunnable(() -> System.out.println("No emojis to add.")))
//                .then();
//    }

//    public Flux<String> getReactions(int num){
//        String[] stringList = new String[]{"🇦", "🇧", "🇨", "🇩"};
//
//    }

    public String stringAnswers(int number){
        return "\"\uD83C\uDDE6\", \"\uD83C\uDDE7\", \"\uD83C\uDDE8\", \"\uD83C\uDDE9\"";
    }
}
