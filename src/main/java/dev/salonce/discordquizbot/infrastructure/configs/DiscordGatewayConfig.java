package dev.salonce.discordquizbot.infrastructure.configs;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DiscordGatewayConfig {

    @Value("${discord.bot.token}")
    private String discordBotToken;

    @Bean
    public GatewayDiscordClient discordGateway() {
        GatewayDiscordClient gateway = DiscordClient.create(discordBotToken)
                .login()
                .block();

        if (gateway == null) {
            throw new IllegalStateException("Failed to connect to Discord gateway. Check your bot token.");
        }

        return gateway;
    }
}
