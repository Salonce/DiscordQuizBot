package dev.salonce.discordQuizBot;

import dev.salonce.discordQuizBot.Buttons.AnswerInteractionEnum;
import dev.salonce.discordQuizBot.Buttons.ButtonInteraction;
import dev.salonce.discordQuizBot.Buttons.ButtonInteractionData;
import dev.salonce.discordQuizBot.Core.Messages.DiscordMessage;
import dev.salonce.discordQuizBot.Core.Messages.MessageHandlerChain;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;


@SpringBootApplication
public class DiscordQuizBotApplication implements CommandLineRunner {

	public DiscordQuizBotApplication(MessageHandlerChain messageHandlerChain, QuizManager quizManager){
		this.messageHandlerChain = messageHandlerChain;
		this.quizManager = quizManager;
	}

	private final MessageHandlerChain messageHandlerChain;
	private final QuizManager quizManager;

	@Value("${discord.bot.token}")
	private String discordBotToken;

	public static void main(String[] args) throws IOException {
		SpringApplication.run(DiscordQuizBotApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception{

		final DiscordClient client = DiscordClient.create(discordBotToken);
		final GatewayDiscordClient gateway = client.login().block();

        if (gateway != null) {
			gateway.on(MessageCreateEvent.class)
					.map(MessageCreateEvent::getMessage)
					.map(DiscordMessage::new)
					.doOnNext(messageHandlerChain::handle)
					.subscribe();

			gateway.on(ButtonInteractionEvent.class, event -> {
				//if button interaction failed right before disabling, there is no message sent even at the beginning of buttonInteraction event,
				// which means it just fails right before the blocking and i can't work on that
				// maybe also check event interaction date to not process anything too old
				//System.out.println("Button event clicked.");
				String buttonId = event.getCustomId();
				//System.out.println("1button id: " + buttonId);
				ButtonInteraction buttonInteraction = new ButtonInteraction(event);
				if (!buttonInteraction.buttonEventValid())
					return null;
				ButtonInteractionData buttonInteractionData = new ButtonInteractionData(buttonId);

				System.out.println("2button id: " + buttonId);
				System.out.println("2button type: " + buttonInteractionData.getButtonType());

				//System.out.println("Button clicked type:" + buttonInteractionData.getButtonType());
				return switch (buttonInteractionData.getButtonType()) {

					case "joinQuiz" -> {
						String joiningText = quizManager.addUserToMatch(buttonInteraction);
						yield event.reply(joiningText).withEphemeral(true);
					}
					case "leaveQuiz" -> {
						String leavingText = quizManager.removeUserFromMatch(buttonInteraction);
						yield event.reply(leavingText).withEphemeral(true);
					}
					case "cancelQuiz" -> {
						boolean canceled = quizManager.cancelQuiz(buttonInteraction);
						String text;
						if (canceled) text = "You've canceled the quiz.";
						else text = "Only matchmaker can cancel the quiz.";
						yield event.reply(text).withEphemeral(true);
					}
					
					case "Answer" -> {
						AnswerInteractionEnum answerInteractionEnum = quizManager.setPlayerAnswer(buttonInteraction, buttonInteractionData);
						String answer;
						if (answerInteractionEnum == AnswerInteractionEnum.NOT_IN_MATCH)
							answer = "You are not in the match.";
						else if (answerInteractionEnum == AnswerInteractionEnum.TOO_LATE)
							answer = "Your answer came too late!";
						else if (answerInteractionEnum == AnswerInteractionEnum.VALID){
							answer = "Your answer: " + (char)('A' + (buttonInteractionData.getAnswerNumber())) + ".";
						}
						else answer = "Something went wrong.";
						yield event.reply(answer).withEphemeral(true);
					}
					default -> event.reply("Button interaction failed. Is it old?").withEphemeral(true);
				};
			}).subscribe();

			gateway.onDisconnect().block();
		}

    }
}
