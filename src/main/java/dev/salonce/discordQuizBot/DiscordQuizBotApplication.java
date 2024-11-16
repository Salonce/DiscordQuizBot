package dev.salonce.discordQuizBot;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.salonce.discordQuizBot.Core.Answer;
import dev.salonce.discordQuizBot.Core.Question;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.util.List;

@SpringBootApplication
public class DiscordQuizBotApplication implements CommandLineRunner {

	@Value("${discord.bot.token}")
	private String discordBotToken;

	public static void main(String[] args) throws IOException {
		SpringApplication.run(DiscordQuizBotApplication.class, args);


		ObjectMapper mapper = new ObjectMapper();
		File file = new File("src/main/resources/java.json");

		Question[] questions = mapper.readValue(file, Question[].class);


		for (Question question : questions) {
			System.out.println(question.getQuestion());
			List<Answer> answers = question.getAnswers();

			for (Answer answer : answers) {
				System.out.println(answer.answer() + ", " + answer.correctness());
			}
			System.out.println();
		}
	}

	@Override
	public void run(String... args) throws Exception{

		final DiscordClient client = DiscordClient.create(discordBotToken);
		final GatewayDiscordClient gateway = client.login().block();


//		gateway.on(MessageCreateEvent.class).subscribe(event -> {
//			Message message = new Message();
//			message.setMessage(event.getMessage());
//
//			messageHandlerChain.handle(message);
//		});
		gateway.onDisconnect().block();
	}
}
