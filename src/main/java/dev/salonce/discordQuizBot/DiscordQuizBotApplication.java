package dev.salonce.discordQuizBot;

import dev.salonce.discordQuizBot.Core.Matches.QuizManager;
import dev.salonce.discordQuizBot.Core.Messages.DiscordMessage;
import dev.salonce.discordQuizBot.Core.Questions.RawQuestion;
import dev.salonce.discordQuizBot.Core.Messages.MessageHandlerChain;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.List;


@SpringBootApplication
public class DiscordQuizBotApplication implements CommandLineRunner {

	public DiscordQuizBotApplication(MessageHandlerChain messageHandlerChain, @Qualifier("javaQuestions") List<RawQuestion> javaQuestions, QuizManager quizManager){
		this.messageHandlerChain = messageHandlerChain;
		this.javaQuestions = javaQuestions;
		this.quizManager = quizManager;
	}

	private final MessageHandlerChain messageHandlerChain;
	private final List<RawQuestion> javaQuestions;
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
				String buttonId = event.getCustomId();
				ButtonInteraction buttonInteraction = new ButtonInteraction(event);
				if (!buttonInteraction.buttonEventValid())
					return null;

				return switch (buttonId) {
					case "joinQuiz" -> {
						quizManager.addUserToMatch(buttonInteraction);
						yield event.reply("You've joined the quiz.").withEphemeral(true);
					}
					case "leaveQuiz" -> {
						quizManager.removeUserFromMatch(buttonInteraction);
						yield event.reply("You've left the quiz.").withEphemeral(true);
					}
					case "answerA" -> {
						boolean inMatch = quizManager.setPlayerAnswer(buttonInteraction, 0);
						String answer = (inMatch) ? "Your answer: A." : "You are not in the match.";
						yield event.reply(answer).withEphemeral(true);
					}
					case "answerB" -> {
						boolean inMatch = quizManager.setPlayerAnswer(buttonInteraction, 1);
						String answer = (inMatch) ? "Your answer: B." : "You are not in the match.";
						yield event.reply(answer).withEphemeral(true);
					}
					case "answerC" -> {
						boolean inMatch = quizManager.setPlayerAnswer(buttonInteraction, 2);
						String answer = (inMatch) ? "Your answer: C." : "You are not in the match.";
						yield event.reply(answer).withEphemeral(true);
					}
					case "answerD" -> {
						boolean inMatch = quizManager.setPlayerAnswer(buttonInteraction, 3);
						String answer = (inMatch) ? "Your answer: D." : "You are not in the match.";
						yield event.reply(answer).withEphemeral(true);
					}
					default -> event.reply("Unknown button interaction").withEphemeral(true);
				};
			}).subscribe();

			gateway.onDisconnect().block();
		}

    }
}
