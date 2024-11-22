package dev.salonce.discordQuizBot.Util;

import dev.salonce.discordQuizBot.Core.DiscordMessage;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class MessageSender {



    public Mono<Message> sendFullChannelMessage(MessageChannel messageChannel, String title, String text) {
        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title(title)
                .description(text)
                .build();
        return sendMessage(messageChannel, embed);
    }

    public Mono<Message> sendMessage(DiscordMessage incomingDiscordMessage, String text) {
        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .author(incomingDiscordMessage.getUserName(), null, incomingDiscordMessage.getUserAvatarUrl())
                .title(text)
                .build();
        return sendMessage(incomingDiscordMessage.getChannel(), embed);
    }

    public Mono<Message> sendChannelMessage(MessageChannel messageChannel, String text) {
        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title(text)
                .build();
        return sendMessage(messageChannel, embed);
    }

    public Mono<Message> sendMessage(MessageChannel messageChannel, EmbedCreateSpec embedMessage) {
        return messageChannel.createMessage(embedMessage);

    }


}
