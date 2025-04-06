package dev.salonce.discordQuizBot.Core;

import dev.salonce.discordQuizBot.Buttons.AnswerInteractionEnum;
import dev.salonce.discordQuizBot.Buttons.ButtonInteraction;
import dev.salonce.discordQuizBot.Buttons.ButtonInteractionData;
import dev.salonce.discordQuizBot.Buttons.ButtonInteractions;
import dev.salonce.discordQuizBot.Core.MessagesHandling.DiscordMessage;
import dev.salonce.discordQuizBot.Core.MessagesHandling.MessageHandlerChain;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BotService {

    private final ButtonInteractions buttonInteractions;
    private final MessageHandlerChain messageHandlerChain;

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

    private void handleButtonInteractions(GatewayDiscordClient gateway) {
        gateway.on(ButtonInteractionEvent.class, event -> {
            ButtonInteraction buttonInteraction = new ButtonInteraction(event);
            if (!buttonInteraction.buttonEventValid()) return null;

            ButtonInteractionData buttonInteractionData = new ButtonInteractionData(event.getCustomId());

            return switch (buttonInteractionData.getButtonType()) {
                case "joinQuiz" -> event.reply(buttonInteractions.addUserToMatch(buttonInteraction)).withEphemeral(true);
                case "leaveQuiz" -> event.reply(buttonInteractions.removeUserFromMatch(buttonInteraction)).withEphemeral(true);
                case "startNow" -> event.reply(buttonInteractions.startNow(buttonInteraction)).withEphemeral(true);
                case "cancelQuiz" -> {
                    boolean canceled = buttonInteractions.cancelQuiz(buttonInteraction);
                    yield event.reply(canceled ? "You've canceled the quiz." : "Only matchmaker can cancel the quiz.").withEphemeral(true);
                }
                case "Answer" -> {
                    AnswerInteractionEnum answerEnum = buttonInteractions.setPlayerAnswer(buttonInteraction);
                    String response = switch (answerEnum) {
                        case NOT_IN_MATCH -> "You are not in the match.";
                        case TOO_LATE -> "Your answer came too late!";
                        case VALID -> "Your answer: " + (char) ('A' + buttonInteractionData.getAnswerNumber()) + ".";
                        default -> "Something went wrong.";
                    };
                    yield event.reply(response).withEphemeral(true);
                }
                default -> event.reply("Button interaction failed. Is it old?").withEphemeral(true);
            };
        }).subscribe();
    }
}