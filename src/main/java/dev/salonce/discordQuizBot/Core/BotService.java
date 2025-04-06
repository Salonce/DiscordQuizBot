package dev.salonce.discordQuizBot.Core;

import dev.salonce.discordQuizBot.Buttons.AnswerInteractionEnum;
import dev.salonce.discordQuizBot.Buttons.ButtonInteraction;
import dev.salonce.discordQuizBot.Buttons.ButtonInteractionData;
import dev.salonce.discordQuizBot.Buttons.ButtonInteractions;
import dev.salonce.discordQuizBot.Buttons.Handlers.ButtonHandlerChain;
import dev.salonce.discordQuizBot.Core.MessagesHandling.DiscordMessage;
import dev.salonce.discordQuizBot.Core.MessagesHandling.MessageHandlerChain;
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
public class BotService {

    private final ButtonInteractions buttonInteractions;
    private final MessageHandlerChain messageHandlerChain;
    private final ButtonHandlerChain buttonHandlerChain;

    @Value("${discord.bot.token}")
    private String discordBotToken;

    public void startBot() {
        final DiscordClient client = DiscordClient.create(discordBotToken);
        final GatewayDiscordClient gateway = client.login().block();

        if (gateway != null) {
            handleMessages(gateway);
            handleButtonInteractions(gateway);

            gateway.onDisconnect().block();
        }
    }

    private void handleMessages(GatewayDiscordClient gateway) {
        gateway.on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .map(DiscordMessage::new)
                .doOnNext(messageHandlerChain::handle)
                .subscribe();
    }

    public void handleButtonInteractions(GatewayDiscordClient gateway) {
        gateway.on(ButtonInteractionEvent.class, event -> {
            // Create button interaction object
            ButtonInteraction buttonInteraction = new ButtonInteraction(event);
            if (!buttonInteraction.buttonEventValid()) {
                return Mono.empty();
            }

            ButtonInteractionData buttonInteractionData = new ButtonInteractionData(event.getCustomId());

            buttonHandlerChain.handle(event, buttonInteraction, buttonInteractionData);

            return Mono.empty(); // Since handlers subscribe to the events themselves
        }).subscribe();
    }
}