package dev.salonce.discordQuizBot;

import dev.salonce.discordQuizBot.Core.Messages.DiscordMessage;
import dev.salonce.discordQuizBot.Core.Questions.RawQuestion;
import dev.salonce.discordQuizBot.Core.Messages.MessageHandlerChain;
import discord4j.common.util.Snowflake;
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

			gateway.onDisconnect().block();
		}


        gateway.on(ButtonInteractionEvent.class, event -> {
            String customId = event.getCustomId();
            Snowflake userId = event.getInteraction().getUser().getId();

//			Mono<MessageChannel> monoChannel = event.getMessage().get().getChannel();
//			Snowflake channelId = monoChannel.block().getId();

            return switch (customId) {
                case "join" -> handleJoin(event, userId);
                case "leave" -> handleLeave(event, userId);
                default -> event.reply("Unknown button interaction").withEphemeral(true);
            };
        });
    }
}
