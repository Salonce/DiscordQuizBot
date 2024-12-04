package dev.salonce.discordQuizBot;

import dev.salonce.discordQuizBot.Core.Messages.DiscordMessage;
import dev.salonce.discordQuizBot.Core.Questions.RawQuestion;
import dev.salonce.discordQuizBot.Core.Messages.MessageHandlerChain;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;


@SpringBootApplication
public class DiscordQuizBotApplication implements CommandLineRunner {

	public DiscordQuizBotApplication(MessageHandlerChain messageHandlerChain, @Qualifier("javaQuestions") List<RawQuestion> javaQuestions){
		this.messageHandlerChain = messageHandlerChain;
		this.javaQuestions = javaQuestions;
	}
	private final MessageHandlerChain messageHandlerChain;
	private final List<RawQuestion> javaQuestions;

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
				String customId = event.getCustomId();
				MessageChannel messageChannel = event.getMessage().get().getChannel().blockOptional().orElse(null);
				if (messageChannel != null) {
					System.out.println("Interaction channel doesn't exist. Something went wrong.");
				}

				return switch (customId) {
//                	case "joinQuiz" -> handleJoin(event, userId);
					case "joinQuiz" -> event.reply("You've joined the quiz.").withEphemeral(true);
					case "leaveQuiz" -> event.reply("You've left the quiz.").withEphemeral(false);
					default -> event.reply("Unknown button interaction").withEphemeral(true);
				};
			}).subscribe();

			gateway.onDisconnect().block();
		}



    }
}
