package dev.salonce.discordQuizBot.MessageHandlers.handlers;

import dev.salonce.discordQuizBot.Core.DiscordMessage;
import dev.salonce.discordQuizBot.Core.MatchMaker;
import dev.salonce.discordQuizBot.Core.MatchService;
import dev.salonce.discordQuizBot.Core.Player;
import dev.salonce.discordQuizBot.MessageHandlers.MessageHandler;
import dev.salonce.discordQuizBot.Util.MessageSender;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Component("startQuiz")
@RequiredArgsConstructor
public class StartQuiz implements MessageHandler {
    private final MessageSender messageSender;
    private final MatchMaker matchMaker;
    private final MatchService matchService;

    @Override
    public boolean handleMessage(DiscordMessage discordMessage) {
        if (discordMessage.getContent().equalsIgnoreCase("qq quiz")) {
            messageSender.sendMessage(discordMessage, "Starting quiz. Click the door button to participate.")
                    .flatMap(message -> addDoorReaction(message).thenReturn(message))
                    .delayElement(Duration.ofSeconds(10))
                    .flatMap(message ->
                            message.getReactors(ReactionEmoji.unicode("\uD83D\uDEAA"))
                                    .map(user -> user.getId().asLong())
                                    .map(id -> new Player(id))
                                    .collectList()
                                    .zipWith(message.getChannel())
                                    .map(tuple -> {
                                        List<Player> players = tuple.getT1();
                                        MessageChannel channel = tuple.getT2();
                                        return matchMaker.javaMatch(players, channel);
                                    })
                    )
                    .doOnNext(matchService::startMatch)
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
