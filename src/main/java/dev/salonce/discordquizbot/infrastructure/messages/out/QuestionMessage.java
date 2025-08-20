package dev.salonce.discordquizbot.infrastructure.messages.out;

import dev.salonce.discordquizbot.domain.Answer;
import dev.salonce.discordquizbot.domain.Match;
import dev.salonce.discordquizbot.application.MatchService;
import dev.salonce.discordquizbot.domain.Question;
import dev.salonce.discordquizbot.domain.Option;
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

import static dev.salonce.discordquizbot.util.DiscordFormatter.formatMentions;

@RequiredArgsConstructor
@Component
public class QuestionMessage {

    private final MatchService matchService;

    public MessageCreateSpec createEmbed(Match match, int timeLeft){
        int answersSize = match.getCurrentQuestion().getOptions().size();

        List<Button> buttons = new ArrayList<>();
        for (int i = 0; i < answersSize; i++) {
            buttons.add(Button.success("Answer-" + (char)('A' + i) + "-" + match.getCurrentQuestionIndex(), String.valueOf((char)('A' + i))));
        }
        buttons.add(Button.danger("cancelQuiz", "Abort quiz"));

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title(titleString(match))
                .addField("\n", "❓ **" + match.getCurrentQuestion().getQuestion() + "**", false)
                .addField("\n", getOptionsString(match.getCurrentQuestion().getOptions()) + "\n", false)
                .addField("\n", "```⏳ " + timeLeft + " seconds left.```", false)
                .build();

        return MessageCreateSpec.builder()
                .addComponent(ActionRow.of(buttons))
                .addEmbed(embed)
                .build();
    }



    public MessageEditSpec editEmbedWithTime(Match match, int timeLeft){
        int answersSize = match.getCurrentQuestion().getOptions().size();

        List<Button> buttons = new ArrayList<>();
        for (int i = 0; i < answersSize; i++) {
            buttons.add(Button.success("Answer-" + (char)('A' + i) + "-" + match.getCurrentQuestionIndex(), String.valueOf((char)('A' + i))));
        }
        buttons.add(Button.danger("cancelQuiz", "Abort quiz"));

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title(titleString(match))
                .addField("\n", "❓ **" + match.getCurrentQuestion().getQuestion() + "**", false)
                .addField("\n", getOptionsString(match.getCurrentQuestion().getOptions()) + "\n", false)
                .addField("\n", "```⏳ " + timeLeft + " seconds left.```", false)
                .build();

        return MessageEditSpec.builder()
                .addComponent(ActionRow.of(buttons))
                .addEmbed(embed)
                .build();
    }

    public MessageEditSpec editEmbedAfterAnswersWait(Match match){
        int answersSize = match.getCurrentQuestion().getOptions().size();

        List<Button> buttons = new ArrayList<>();
        for (int i = 0; i < answersSize; i++) {
            buttons.add(Button.success("Answer-" + (char)('A' + i) + "-" + match.getCurrentQuestionIndex(), String.valueOf((char)('A' + i))).disabled());
        }
        buttons.add(Button.danger("cancelQuiz", "Abort quiz"));

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title(titleString(match))
                .addField("\n", "❓ **" + match.getCurrentQuestion().getQuestion() + "**", false)
                .addField("\n", getOptionsString(match.getCurrentQuestion().getOptions()) + "\n", false)
                .build();

        return MessageEditSpec.builder()
                .addComponent(ActionRow.of(buttons))
                .addEmbed(embed)
                .build();
    }

    public MessageEditSpec editEmbedWithScores(Match match){
        int answersSize = match.getCurrentQuestion().getOptions().size();

        List<Button> buttons = new ArrayList<>();
        for (int i = 0; i < answersSize; i++) {
            buttons.add(Button.success("Answer-" + (char)('A' + i) + "-" + match.getCurrentQuestionIndex(), String.valueOf((char)('A' + i))).disabled());
        }
        buttons.add(Button.danger("cancelQuiz", "Abort quiz"));

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title(titleString(match))
                .addField("\n", "❓ **" + match.getCurrentQuestion().getQuestion() + "**", false)
                .addField("\n", getOptionsRevealed(match.getCurrentQuestion().getOptions()) + "\n", false)
                .addField("\uD83D\uDCDD Explanation", match.getCurrentQuestion().getExplanation() + "\n", false)
                .addField("\uD83D\uDCCB Answers", getUsersAnswers(match), false)
                .addField("\uD83D\uDCCA Scoreboard", getScoreboard(match), false)
                .build();

        return MessageEditSpec.builder()
                .addComponent(ActionRow.of(buttons))
                .addEmbed(embed)
                .build();
    }

    private String getOptionsRevealed(List<Option> options){
        StringBuilder sb = new StringBuilder();
        char letter = 'A';
        for (Option option : options){
            if (!option.isCorrect()) sb.append("❌ ").append(letter).append(") ").append(option.text());
            if (option.isCorrect()) sb.append("✅** ").append(letter).append(") ").append(option.text()).append("**");
            letter++;
            sb.append("\n");
        }
        return sb.toString();
    }

    private String titleString(Match match){
        return "Question " + (match.getCurrentQuestionIndex() + 1) + "/10";
    }
    private String getUsersAnswers(Match match) {
        Question currentQuestion = match.getCurrentQuestion();
        int optionsLength = currentQuestion.getOptions().size();
        Map<Answer, List<Long>> playerGroups = match.getPlayersGroupedByAnswer();
        Answer answer = currentQuestion.getCorrectAnswer();

        StringBuilder sb = new StringBuilder();

        // Format each option
        for (int i = 0; i < optionsLength; i++) {

            if (i > 0) sb.append("\n");

            String prefix = answer.asNumber() == i ? "✅ **" + (char)('A' + i) + "**"
                                                   : "❌ " + (char)('A' + i);
            sb.append(prefix).append(": ");
            List<Long> playerIds = playerGroups.getOrDefault(Answer.fromNumber(i), Collections.emptyList());
            sb.append(formatMentions(playerIds));
        }

        // Non-responders
        sb.append("\n\n💤: ");
        List<Long> playerIds = playerGroups.getOrDefault(Answer.none(), Collections.emptyList());
        sb.append(formatMentions(playerIds));

        return sb.toString();
    }

    private String getOptionsString(List<Option> options){
        StringBuilder sb = new StringBuilder();
        char letter = 'A';
        for (Option option : options){
            sb.append(letter).append(") ").append(option.text());
            letter++;
            sb.append("\n");
        }
        return sb.toString();
    }

    private String getScoreboard(Match match) {
        Map<Long, Long> points = match.getPlayersPoints(); // use your new map

        return points.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue())) // sort descending
                .map(entry -> "<@" + entry.getKey() + ">: " + entry.getValue() + " points")
                .collect(Collectors.joining("\n"));
    }


}
