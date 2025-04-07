package dev.salonce.discordQuizBot;

import dev.salonce.discordQuizBot.Core.BotService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.io.IOException;

@RequiredArgsConstructor
@SpringBootApplication
public class DiscordQuizBotApplication implements CommandLineRunner {

	private final BotService botService;

	public static void main(String[] args) throws IOException {
		// loading two application files, normal overriding sample if it exists
		new SpringApplicationBuilder(DiscordQuizBotApplication.class)
				.properties("spring.config.name=application,application-sample",
						"spring.config.additional-location=classpath:/sample/,classpath:/private/")

				.run(args);
	}

	@Override
	public void run(String... args) {
		botService.startBot();
	}

}
