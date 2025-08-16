package dev.salonce.discordquizbot.infrastructure.messages.out;

import dev.salonce.discordquizbot.domain.Match;
import dev.salonce.discordquizbot.application.MatchService;
import dev.salonce.discordquizbot.domain.Player;
import dev.salonce.discordquizbot.domain.Question;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class QuestionMessage {

    private final MatchService matchService;

    public MessageCreateSpec createEmbed(Match match, Long questionNumber, int timeLeft){
        String questionsAnswers = match.getCurrentQuestion().getOptions();
        int answersSize = match.getCurrentQuestion().getQuizOptions().size();

        List<Button> buttons = new ArrayList<>();
        for (int i = 0; i < answersSize; i++) {
            buttons.add(Button.success("Answer-" + (char)('A' + i) + "-" + questionNumber.toString(), String.valueOf((char)('A' + i))));
        }
        buttons.add(Button.danger("cancelQuiz", "Abort quiz"));

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title(titleString(match))
                .addField("\n", "❓ **" + match.getCurrentQuestion().getQuestion() + "**", false)
                .addField("\n", questionsAnswers + "\n", false)
                .addField("\n", "```⏳ " + timeLeft + " seconds left.```", false)
                .build();

        return MessageCreateSpec.builder()
                .addComponent(ActionRow.of(buttons))
                .addEmbed(embed)
                .build();
    }

    public MessageEditSpec editEmbedWithTime(Match match, Long questionNumber, int timeLeft){
        String questionsAnswers = match.getCurrentQuestion().getOptions();
        int answersSize = match.getCurrentQuestion().getQuizOptions().size();

        List<Button> buttons = new ArrayList<>();
        for (int i = 0; i < answersSize; i++) {
            buttons.add(Button.success("Answer-" + (char)('A' + i) + "-" + questionNumber.toString(), String.valueOf((char)('A' + i))));
        }
        buttons.add(Button.danger("cancelQuiz", "Abort quiz"));

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title(titleString(match))
                .addField("\n", "❓ **" + match.getCurrentQuestion().getQuestion() + "**", false)
                .addField("\n", questionsAnswers + "\n", false)
                .addField("\n", "```⏳ " + timeLeft + " seconds left.```", false)
                .build();

        return MessageEditSpec.builder()
                .addComponent(ActionRow.of(buttons))
                .addEmbed(embed)
                .build();
    }

    public MessageEditSpec editEmbedAfterAnswersWait(Match match, Long questionNumber){
        String questionsAnswers = match.getCurrentQuestion().getOptions();
        int answersSize = match.getCurrentQuestion().getQuizOptions().size();

        List<Button> buttons = new ArrayList<>();
        for (int i = 0; i < answersSize; i++) {
            buttons.add(Button.success("Answer-" + (char)('A' + i) + "-" + questionNumber.toString(), String.valueOf((char)('A' + i))).disabled());
        }
        buttons.add(Button.danger("cancelQuiz", "Abort quiz"));

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title(titleString(match))
                .addField("\n", "❓ **" + match.getCurrentQuestion().getQuestion() + "**", false)
                .addField("\n", questionsAnswers + "\n", false)
                .build();

        return MessageEditSpec.builder()
                .addComponent(ActionRow.of(buttons))
                .addEmbed(embed)
                .build();
    }

    public MessageEditSpec editEmbedWithScores(Match match, Long questionNumber){
        String questionsAnswers = match.getCurrentQuestion().getOptionsRevealed();
        int answersSize = match.getCurrentQuestion().getQuizOptions().size();

        List<Button> buttons = new ArrayList<>();
        for (int i = 0; i < answersSize; i++) {
            buttons.add(Button.success("Answer-" + (char)('A' + i) + "-" + questionNumber.toString(), String.valueOf((char)('A' + i))).disabled());
        }
        buttons.add(Button.danger("cancelQuiz", "Abort quiz"));

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title(titleString(match))
                .addField("\n", "❓ **" + match.getCurrentQuestion().getQuestion() + "**", false)
                .addField("\n", questionsAnswers + "\n", false)
                .addField("\uD83D\uDCDD Explanation", match.getCurrentQuestion().getExplanation() + "\n", false)
                .addField("\uD83D\uDCCB Answers", getUsersAnswers(match), false)
                .addField("\uD83D\uDCCA Scoreboard", getScoreboard(match), false)
                .build();

        return MessageEditSpec.builder()
                .addComponent(ActionRow.of(buttons))
                .addEmbed(embed)
                .build();
    }

    private String titleString(Match match){
        return "Question " + (match.getCurrentQuestionNum() + 1) + "/10";
    }
    private String getUsersAnswers(Match match) {
        Question currentQuestion = match.getCurrentQuestion();
        Map<Integer, List<Long>> playerGroups = match.getPlayersGroupedByAnswer();
        int correctAnswer = currentQuestion.getCorrectAnswerInt();

        StringBuilder sb = new StringBuilder();

        // Format each option
        for (int i = 0; i < currentQuestion.getQuizOptions().size(); i++) {
            if (i > 0) sb.append("\n");

            String prefix = correctAnswer == i ? "✅ **" + (char)('A' + i) + "**" : "❌ " + (char)('A' + i);
            sb.append(prefix).append(": ");

            List<Long> playerIds = playerGroups.getOrDefault(i, Collections.emptyList());
            String mentions = playerIds.stream()
                    .map(id -> "<@" + id + ">")
                    .collect(Collectors.joining(", "));
            sb.append(mentions);
        }

        // Non-responders
        sb.append("\n\n💤: ");
        List<Long> nonResponders = playerGroups.getOrDefault(-1, Collections.emptyList());
        String nonResponderMentions = nonResponders.stream()
                .map(id -> "<@" + id + ">")
                .collect(Collectors.joining(", "));
        sb.append(nonResponderMentions);

        return sb.toString();
    }

    private String getScoreboard(Match match){
        return match.getPlayers().entrySet().stream().sorted((a, b) -> (b.getValue().getPoints() - a.getValue().getPoints())).map(entry -> "<@" + entry.getKey() + ">" + ": " + entry.getValue().getPoints() + " points").collect(Collectors.joining("\n"));
    }
}
