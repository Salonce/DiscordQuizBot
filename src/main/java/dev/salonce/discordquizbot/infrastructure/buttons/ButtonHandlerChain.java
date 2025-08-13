package dev.salonce.discordquizbot.infrastructure.buttons;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ButtonHandlerChain {
    private final List<ButtonHandler> buttonHandlers;

    public void handle(ButtonInteractionEvent event, ButtonInteractionData buttonInteractionData) {
        for (ButtonHandler handler : buttonHandlers) {
            //System.out.println("handling: " + handler.getClass());
            if (handler.handle(event, buttonInteractionData)) {
                break;
            }
        }
    }


//    public static ButtonHandlerChain createStandardChain(ButtonInteractions buttonInteractions) {
//        List<ButtonHandler> handlers = new ArrayList<>();
//
//        // Add handlers in priority order
//        handlers.add(new JoinQuizButtonHandler(buttonInteractions));
//        handlers.add(new LeaveQuizButtonHandler(buttonInteractions));
//        handlers.add(new StartNowButtonHandler(buttonInteractions));
//        handlers.add(new CancelMatchButtonHandler(buttonInteractions));
//        handlers.add(new AnswerButtonHandler(buttonInteractions));
//        handlers.add(new FallbackButtonHandler());
//
//        return new ButtonHandlerChain(handlers);
//    }
}
