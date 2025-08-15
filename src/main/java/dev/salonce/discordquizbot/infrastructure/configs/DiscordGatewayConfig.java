package dev.salonce.discordquizbot.infrastructure.configs;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DiscordGatewayConfig {

    @Value("${discord.bot.token}")
    private String discordBotToken;

    public GatewayDiscordClient getDiscordGateway(){
        return DiscordClient.create(discordBotToken).login().block();
    }
}
