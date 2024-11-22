package dev.salonce.discordQuizBot.Core.Messages;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.Getter;

import java.util.NoSuchElementException;


@Getter
public class DiscordMessage {
    public DiscordMessage(Message message) {
        try {
            //author, maybe change optional and throw
            this.id = message.getAuthor().get().getId().asLong();
            this.userNameId = "<@" + message.getAuthor().get().getId().asString() + ">";
            this.userAvatarUrl = message.getAuthor().get().getAvatarUrl();
            this.userName = message.getAuthor().get().getUsername();

            this.channel = message.getChannel().block();
            this.content = message.getContent();

        } catch (NoSuchElementException noSuchElementException) {
            System.out.println("Error setting message data.");
        }
    }

    private Long id;
    private String content;
    private MessageChannel channel;
    private String userName;
    private String userNameId;
    private String userAvatarUrl;
}

