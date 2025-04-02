package dev.salonce.discordQuizBot;

import dev.salonce.discordQuizBot.Core.BotService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@RequiredArgsConstructor
@SpringBootApplication
public class DiscordQuizBotApplication implements CommandLineRunner {

	private final BotService botService;

	public static void main(String[] args) throws IOException {
		SpringApplication.run(DiscordQuizBotApplication.class, args);
	}

	@Override
	public void run(String... args) {
		botService.startBot();
	}

}
