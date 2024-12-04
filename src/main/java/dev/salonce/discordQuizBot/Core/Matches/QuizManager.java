package dev.salonce.discordQuizBot.Core.Matches;

import dev.salonce.discordQuizBot.Core.Messages.MessageSender;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuizManager {

    private final Map<MessageChannel, Match> quizzes;

    public QuizManager(){
        quizzes = new HashMap<>();
    }

    public void addUserToMatch(MessageChannel messageChannel, User user){
        if (quizzes.containsKey(messageChannel)){
            if (quizzes.get(messageChannel).addPlayer(user)) {
                //send message to the message channel that user is added
            }
            else{
                //send message to the message channel that interacting failed because match doesn't exist?
            }
        }
    }

    @Autowired
    private MessageSender messageSender;

    public void addMatch(MessageChannel messageChannel, Match match) {
        if (quizzes.containsKey(messageChannel)){
            //send message that starting a match is impossible because there is already one
        }
        else{
            quizzes.put(messageChannel, match);
            sendStartQuizMessage(messageChannel).subscribe();
//          messageSender.sendChannelMessage(messageChannel, matchParticipants(match.getPlayers())).subscribe();
//          sendSpecMessage(messageChannel, matchParticipants(match.getPlayers()));
        }
    }


    public Mono<Message> sendStartQuizMessage(MessageChannel messageChannel){
        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("Java quiz")
                .description("Click the button to participate.")
                .build();

        MessageCreateSpec spec = MessageCreateSpec.builder()
                .addComponent(ActionRow.of(Button.success("joinQuiz", "Join"), Button.success("leaveQuiz", "Leave")))
                .addEmbed(embed)
                .build();

        return messageChannel.createMessage(spec);
    }

    //String listing match participants
    private String matchParticipants(List<Player> playerMatchData){
        StringBuilder stringBuilder = new StringBuilder("Match participants: ");
        if (!playerMatchData.isEmpty()) {
            stringBuilder.append(playerMatchData.get(0).getUser().getMention());
            for (int i = 1; i < playerMatchData.size(); i++){
                stringBuilder.append(", ");
                stringBuilder.append(playerMatchData.get(i));
            }
            stringBuilder.append(".");
        }
        else{
            stringBuilder.append("none.");
        }

        return stringBuilder.toString();
    }

    private void sendParticipantsMessage(MessageChannel messageChannel, String participants){
        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("Participants")
                //.description("Some participants")
                .description(participants)
                .build();

        MessageCreateSpec spec = MessageCreateSpec.builder()
                .addEmbed(embed)
                .build();

        messageChannel.createMessage(spec).subscribe();
    }
}



//    public void newMatch(Snowflake channelId, Match match){
//        quizzes.put(channelId, match);
//    }