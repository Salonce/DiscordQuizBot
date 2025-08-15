package dev.salonce.discordquizbot.infrastructure.dtos;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.Getter;

import java.util.NoSuchElementException;


public record DiscordMessage(MessageChannel channel, User user, String userNameId, String userName, Long usernameIdLong, String userAvatarUrl, String content){}

