package dev.salonce.discordQuizBot.Core.Matches;

import dev.salonce.discordQuizBot.Core.Messages.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MessageSender messageSender;

    public void startMatch(Match match) {
        messageSender.sendChannelMessage(match.getMessageChannel(), matchParticipants(match.getPlayers())).subscribe();
        sendSpecMessage(match.getMessageChannel(), matchParticipants(match.getPlayers()));
    }

//
//    private String matchParticipants(List<Player> players){
//        StringBuilder stringBuilder = new StringBuilder("Match participants: ");
//        if (!players.isEmpty()) {
//            stringBuilder.append(players.get(0).getUser().getMention());
//            for (int i = 1; i < players.size(); i++){
//                stringBuilder.append(", ");
//                stringBuilder.append(players.get(i));
//            }
//            stringBuilder.append(".");
//        }
//        else{
//            stringBuilder.append("none.");
//        }
//
//        return stringBuilder.toString();
//    }


//    public void sendSpecMessage(MessageChannel messageChannel, String participants){
//        EmbedCreateSpec embed = EmbedCreateSpec.builder()
//                .title("Participants")
//                //.description("Some participants")
//                .description(participants)
//                .build();
//
//        MessageCreateSpec spec = MessageCreateSpec.builder()
//                .addComponent(ActionRow.of(Button.success("Join", "Join!"), Button.success("Join not", "Don't join.")))
//                .addEmbed(embed)
//                .build();
//
//        messageChannel.createMessage(spec).subscribe();
//    }
}