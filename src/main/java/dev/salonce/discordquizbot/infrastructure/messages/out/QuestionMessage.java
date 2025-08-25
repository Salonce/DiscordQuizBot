package dev.salonce.discordquizbot.infrastructure.messages.out;

import dev.salonce.discordquizbot.domain.*;
import dev.salonce.discordquizbot.application.MatchService;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static dev.salonce.discordquizbot.infrastructure.util.DiscordFormatter.formatMentions;

@RequiredArgsConstructor
@Component
public class QuestionMessage {

    private final MatchService matchService;

    public MessageCreateSpec createEmbed(Match match, int timeLeft){
        int answersSize = match.getCurrentQuestion().getOptions().size();

        List<Button> buttons = new ArrayList<>();
        for (int i = 0; i < answersSize; i++) {
            buttons.add(Button.success("Answer-" + (char)('A' + i) + "-" + match.currentQuestionIndex(), String.valueOf((char)('A' + i))));
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
            buttons.add(Button.success("Answer-" + (char)('A' + i) + "-" + match.currentQuestionIndex(), String.valueOf((char)('A' + i))));
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
            buttons.add(Button.success("Answer-" + (char)('A' + i) + "-" + match.currentQuestionIndex(), String.valueOf((char)('A' + i))).disabled());
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
            buttons.add(Button.success("Answer-" + (char)('A' + i) + "-" + match.currentQuestionIndex(), String.valueOf((char)('A' + i))).disabled());
        }
        buttons.add(Button.danger("cancelQuiz", "Abort quiz"));

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title(titleString(match))
                .addField("\n", "❓ **" + match.getCurrentQuestion().getQuestion() + "**", false)
                .addField("\n", getOptionsRevealed(match.getCurrentQuestion().getOptions()) + "\n", false)
                .addField("\uD83D\uDCDD Explanation", match.getCurrentQuestion().getExplanation() + "\n", false)
                .addField("\uD83D\uDCCB Answers", getUsersAnswers(match.getAnswerDistribution()), false)
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
        return "Question " + (match.currentQuestionIndex() + 1) + "/10";
    }
    private String getUsersAnswers(AnswerDistributionDto distributionDto) {
        List<AnswerOptionGroup> groups = distributionDto.getAnswerGroups();

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < groups.size(); i++) {
            Answer answer = groups.get(i).getAnswer();
            boolean correct = groups.get(i).isCorrect();

            if (i > 0) sb.append("\n");

            String prefix = correct ?
                    "✅ **" + answer.asChar() + "**"
                    : "❌ " + answer.asChar();
            sb.append(prefix).append(": ");

            List<Long> playerIds = groups.get(i).getUserIds();
            sb.append(formatMentions(playerIds));
        }

        sb.append("\n\n💤: ");
        sb.append(formatMentions(distributionDto.getNoAnswerGroup().getUserIds()));

        return sb.toString();
    }

    private String getOptionsString(List<Option> options) {
        return IntStream.range(0, options.size())
                .mapToObj(i -> Answer.fromNumber(i).asChar() + ") " + options.get(i).text())
                .collect(Collectors.joining("\n"));
    }

    private String getScoreboard(Match match) {
        Scoreboard scoreboard = match.getScoreboard();

        return scoreboard.getRankedScores().stream()
                .map(this::formatPlayerScore)
                .collect(Collectors.joining("\n"));
    }

    private String formatPlayerScore(PlayerScore playerScore) {
        String pointsText = playerScore.getPoints() == 1 ? "point" : "points";
        return "<@" + playerScore.getPlayerId() + ">: " +
                playerScore.getPoints() + " " + pointsText;
    }

}
