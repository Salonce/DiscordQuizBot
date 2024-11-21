package dev.salonce.discordQuizBot.Core;

import dev.salonce.discordQuizBot.Util.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MessageSender messageSender;

    public void startMatch(Match match) {
        messageSender.sendChannelMessage(match.getMessageChannel(), "Match participants: " + match.getPlayers()).subscribe();
        // Additional quiz logic
    }


    private String matchParticipants(List<Player> players){
        StringBuilder stringBuilder = new StringBuilder("Match participants: ");
        return stringBuilder.toString();
    }
}