package dev.salonce.discordQuizBot.Buttons;

import dev.salonce.discordQuizBot.Core.MatchStore;
import dev.salonce.discordQuizBot.Core.Matches.Match;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ButtonInteractions {

    private final MatchStore matchStore;

    public boolean cancelQuiz(ButtonInteraction buttonInteraction) {
        MessageChannel messageChannel = buttonInteraction.getMessageChannel();
        Match match = matchStore.get(messageChannel);
        return match.closeMatch(buttonInteraction.getUserId());
    }

    public String addUserToMatch(ButtonInteraction buttonInteraction){
        MessageChannel messageChannel = buttonInteraction.getMessageChannel();
        Match match = matchStore.get(messageChannel);
        int questionsNumber = match.getQuestions().size();

        if (matchStore.containsKey(messageChannel)){
            return matchStore.get(messageChannel).addPlayer(buttonInteraction.getUserId(), questionsNumber);
        }
        else{
            return "This match doesn't exist anymore.";
        }
    }

    public String removeUserFromMatch(ButtonInteraction buttonInteraction){
        MessageChannel messageChannel = buttonInteraction.getMessageChannel();

        if (matchStore.containsKey(messageChannel))
            return matchStore.get(messageChannel).removePlayer(buttonInteraction.getUserId());
        else{
            return "This match doesn't exist anymore.";
        }
    }

    public String startNow(ButtonInteraction buttonInteraction){
        MessageChannel messageChannel = buttonInteraction.getMessageChannel();

        if (matchStore.containsKey(messageChannel)){
            if (!matchStore.get(messageChannel).isStartNow()) {
                matchStore.get(messageChannel).setStartNow(true);
                return "Starting immediately";
            }
            else
                return "Already started";
        }
        else{
            return "This match doesn't exist anymore.";
        }
    }

    public AnswerInteractionEnum setPlayerAnswer(ButtonInteraction buttonInteraction) {
        Long userId = buttonInteraction.getUserId();
        MessageChannel messageChannel = buttonInteraction.getMessageChannel();
        Match match = matchStore.get(messageChannel);
        String buttonId = buttonInteraction.getButtonId();

        int answerNum = 0;
        int questionNum = 0;

        if (buttonId.matches("Answer-[A-D]-\\d+")) {
            String[] parts = buttonId.split("-");

            // Convert letter to text number (A=0, B=1, C=2, D=3)
            char letterPart = parts[1].charAt(0);
            answerNum = letterPart - 'A';

            // Extract question number
            questionNum = Integer.parseInt(parts[2]);
        }

            if (match == null)
                return AnswerInteractionEnum.TOO_LATE; // could be different

            if (questionNum != match.getCurrentQuestionNum() || !match.isAnsweringOpen())
                return AnswerInteractionEnum.TOO_LATE;

            if (match.getPlayers().containsKey(userId)) {
                match.getPlayers().get(userId).getAnswersList().set(questionNum, answerNum);
                return AnswerInteractionEnum.VALID;
            } else return AnswerInteractionEnum.NOT_IN_MATCH;
        }
}