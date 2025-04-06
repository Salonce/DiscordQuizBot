package dev.salonce.discordQuizBot.Buttons.Handlers;

import dev.salonce.discordQuizBot.Buttons.ButtonHandler;
import dev.salonce.discordQuizBot.Buttons.ButtonInteraction;
import dev.salonce.discordQuizBot.Buttons.ButtonInteractionData;
import dev.salonce.discordQuizBot.Buttons.ButtonInteractions;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ButtonHandlerChain {
    private final List<ButtonHandler> buttonHandlers;

    public void handle(ButtonInteractionEvent event, ButtonInteraction buttonInteraction, ButtonInteractionData data) {
        for (ButtonHandler handler : buttonHandlers) {
            if (handler.handle(event, buttonInteraction, data)) {
                break;
            }
        }
    }

    // Factory method to create a standard chain with all handlers
    public static ButtonHandlerChain createStandardChain(ButtonInteractions buttonInteractions) {
        List<ButtonHandler> handlers = new ArrayList<>();

        // Add handlers in priority order
        handlers.add(new JoinQuizButtonHandler(buttonInteractions));
        handlers.add(new LeaveQuizButtonHandler(buttonInteractions));
        handlers.add(new StartNowButtonHandler(buttonInteractions));
        handlers.add(new CancelQuizButtonHandler(buttonInteractions));
        handlers.add(new AnswerButtonHandler(buttonInteractions));
        handlers.add(new FallbackButtonHandler());

        return new ButtonHandlerChain(handlers);
    }
}
