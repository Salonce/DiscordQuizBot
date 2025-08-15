package dev.salonce.discordquizbot.infrastructure;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class DiscordChannelProvider {

    private final GatewayDiscordClient gateway;

    public Mono<MessageChannel> getChannelById(long channelId) {
        return gateway.getChannelById(Snowflake.of(channelId))
                .ofType(MessageChannel.class)
                .switchIfEmpty(Mono.error(
                        new IllegalArgumentException("No MessageChannel found with ID: " + channelId)
                ));
    }
}
