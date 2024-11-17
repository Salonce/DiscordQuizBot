package dev.salonce.discordQuizBot;

import dev.salonce.discordQuizBot.Core.Message;
import dev.salonce.discordQuizBot.Core.RawQuestion;
import dev.salonce.discordQuizBot.MessageHandlers.MessageHandlerChain;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
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

		gateway.on(MessageCreateEvent.class).subscribe(event -> {
			messageHandlerChain.handle(new Message(event.getMessage()));
		});
		gateway.onDisconnect().block();
	}
}
