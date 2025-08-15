package dev.salonce.discordquizbot.infrastructure.bootstrapping;

import dev.salonce.discordquizbot.application.ButtonHandlerChain;
import dev.salonce.discordquizbot.application.MessageHandlerChain;
import dev.salonce.discordquizbot.infrastructure.dtos.ButtonInteractionData;
import dev.salonce.discordquizbot.infrastructure.mappers.ButtonMapper;
import dev.salonce.discordquizbot.infrastructure.mappers.MessageMapper;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class DiscordBotBootstrap {

    private final MessageHandlerChain messageHandlerChain;
    private final ButtonHandlerChain buttonHandlerChain;

    @Value("${discord.bot.token}")
    private String discordBotToken;

    public void startBot() {
        final DiscordClient client = DiscordClient.create(discordBotToken);
        final GatewayDiscordClient gateway = client.login().block();

        if (gateway != null) {

            printGuildCount(gateway);
            handleMessages(gateway);
            handleButtonInteractions(gateway);

            gateway.onDisconnect().block();
        }
    }

    private void handleMessages(GatewayDiscordClient gateway) {
        gateway.on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .flatMap(message -> Mono.justOrEmpty(MessageMapper.toDiscordMessage(message)))
                .doOnNext(messageHandlerChain::handle)
                .subscribe();
    }

    private void handleButtonInteractions(GatewayDiscordClient gateway) {
        gateway.on(ButtonInteractionEvent.class, event -> {
            ButtonInteractionData buttonInteractionData = ButtonMapper.toButtonInteractionData(event);
            if (buttonInteractionData == null)
                return Mono.empty();

            buttonHandlerChain.handle(event, buttonInteractionData);

            return Mono.empty(); // Since handlers subscribe to the events themselves
        }).subscribe();
    }

    private void printGuildCount(GatewayDiscordClient gateway){
        int guildCount = gateway.getGuilds().collectList().block().size();
        System.out.println("Bot is in " + guildCount + " servers.");
    }
}