package dev.salonce.discordQuizBot.Core.Messages.Handlers;

import dev.salonce.discordQuizBot.Core.Messages.DiscordMessage;
import dev.salonce.discordQuizBot.Core.Matches.MatchFactory;
import dev.salonce.discordQuizBot.Core.Matches.QuizManager;
import dev.salonce.discordQuizBot.Core.Messages.MessageHandler;
import dev.salonce.discordQuizBot.Core.Messages.MessageSender;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component("startQuiz")
@RequiredArgsConstructor
public class StartQuiz implements MessageHandler {
    private final MatchFactory matchFactory;
    private final QuizManager quizManager;

    @Override
    public boolean handleMessage(DiscordMessage discordMessage) {
        if (discordMessage.getContent().equalsIgnoreCase("qq quiz java")) {
            MessageChannel messageChannel = discordMessage.getChannel();
            quizManager.addMatch(messageChannel, matchFactory.javaMatch());
            return true;
        }
        return false;
    }

//    public Mono<Message> sendSpecMessage(MessageChannel messageChannel){
//        EmbedCreateSpec embed = EmbedCreateSpec.builder()
//                .title("Java quiz")
//                .description("Click the button to participate.")
//                .build();
//
//        MessageCreateSpec spec = MessageCreateSpec.builder()
//                .addComponent(ActionRow.of(Button.success("Join", "Join!"), Button.success("Join not", "Don't join.")))
//                .addEmbed(embed)
//                .build();
//
//        return messageChannel.createMessage(spec);
//    }
}
