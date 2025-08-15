package dev.salonce.discordquizbot;

import dev.salonce.discordquizbot.infrastructure.bootstrapping.DiscordBotBootstrap;
import dev.salonce.discordquizbot.infrastructure.configs.ApplicationYmlConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@RequiredArgsConstructor
@SpringBootApplication
public class DiscordQuizBotApplication implements CommandLineRunner {

	private final DiscordBotBootstrap discordBotBootstrap;

	public static void main(String[] args) {
		ApplicationYmlConfig.configure();
		SpringApplication.run(DiscordQuizBotApplication.class, args);
	}

	@Override
	public void run(String... args) {
		discordBotBootstrap.startBot();
	}
}
