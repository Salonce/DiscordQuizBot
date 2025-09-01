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
                                .onErrorResume(ClientException.class, ex -> {
                                    if (ex.getStatus().code() == 403) {
                                        log.error("❌ Bot missing permissions in channel {} for message {}",
                                                message.getChannelId().asString(), message.getId().asString());
                                    } else {
                                        log.error("⚠️ Client exception processing message {}", message.getId().asString(), ex);
                                    }
                                    return Mono.empty();
                                })
                                .doOnError(error -> log.error("Failed to handle message {}", message.getId().asString(), error))
                )
                .subscribe();
    }

    private void handleButtonInteractions(GatewayDiscordClient gateway) {
        gateway.on(ButtonInteractionEvent.class, event ->
                Mono.fromCallable(() -> ButtonMapper.toButtonInteractionData(event))
                        .flatMap(data -> Mono.justOrEmpty(buttonHandlerChain.handle(data)))
                        .flatMap(resultStatus -> event.reply(resultStatus.getMessage()).withEphemeral(true))
                        .onErrorResume(ex -> {
                            log.error("Failed to handle button interaction", ex);
                            return Mono.empty();
                        })
        ).subscribe();
    }
}