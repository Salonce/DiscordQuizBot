package dev.salonce.discordquizbot.infrastructure.bootstrapping;

import dev.salonce.discordquizbot.application.ButtonHandlerChain;
import dev.salonce.discordquizbot.application.MessageHandlerChain;
import dev.salonce.discordquizbot.infrastructure.mappers.ButtonMapper;
import dev.salonce.discordquizbot.infrastructure.mappers.MessageMapper;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.rest.http.client.ClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class DiscordBotBootstrap {

    private final MessageHandlerChain messageHandlerChain;
    private final ButtonHandlerChain buttonHandlerChain;
    private final GatewayDiscordClient gateway;

    public void startBot() {
        handleMessages(gateway);
        handleButtonInteractions(gateway);
        gateway.onDisconnect().block();
    }

    private void handleMessages(GatewayDiscordClient gateway) {
        gateway.on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .flatMap(message ->
                        Mono.justOrEmpty(MessageMapper.toDiscordMessage(message))
                                .doOnNext(messageHandlerChain::handle)
                                .onErrorResume(ex -> {
                                    if (ex instanceof ClientException ce && ce.getStatus().code() == 403) {
                                        log.error("❌ Missing permissions in channel {} for message {}",
                                                message.getChannelId().asString(), message.getId().asString());
                                    } else {
                                        log.error("💥 Unexpected error while processing message {} in channel {}",
                                                message.getId().asString(), message.getChannelId().asString(), ex);
                                    }
                                    return Mono.empty();
                                })
                )
                .subscribe();
    }

    private void handleButtonInteractions(GatewayDiscordClient gateway) {
        gateway.on(ButtonInteractionEvent.class, event ->
                Mono.fromCallable(() -> ButtonMapper.toButtonInteractionData(event))
                        .flatMap(data -> Mono.justOrEmpty(buttonHandlerChain.handle(data)))
                        .flatMap(resultStatus ->event.reply(resultStatus.getMessage()).withEphemeral(true))
                        .onErrorResume(ex -> {
                            if (ex instanceof ClientException ce && ce.getStatus().code() == 403) {
                                log.error("❌ Missing permissions to reply to button interaction {} in channel {}",
                                        event.getCustomId(), event.getInteraction().getChannelId().asString());
                            } else {
                                log.error("💥 Unexpected error while handling button interaction {} in channel {}",
                                        event.getCustomId(), event.getInteraction().getChannelId().asString(), ex);
                            }
                            return Mono.empty(); // swallow error, keep bot alive
                        })
        ).subscribe();
    }
}