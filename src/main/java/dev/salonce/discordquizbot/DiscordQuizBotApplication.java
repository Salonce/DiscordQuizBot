package dev.salonce.discordquizbot;

import dev.salonce.discordquizbot.core.BotService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@RequiredArgsConstructor
@SpringBootApplication
public class DiscordQuizBotApplication implements CommandLineRunner {

	private final BotService botService;

	public static void main(String[] args) {
		Resource primaryResource = new ClassPathResource("private/application.yml");

		String configLocation = primaryResource.exists() ?
				"classpath:/private/application.yml" :
				"classpath:/sample/application-sample.yml";

		System.setProperty("spring.config.location", configLocation);
		SpringApplication.run(DiscordQuizBotApplication.class, args);
	}

	@Override
	public void run(String... args) {
		botService.startBot();
	}

}
