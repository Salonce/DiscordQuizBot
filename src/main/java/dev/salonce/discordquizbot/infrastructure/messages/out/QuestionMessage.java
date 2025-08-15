package dev.salonce.discordquizbot.infrastructure.messages.out;

import dev.salonce.discordquizbot.domain.Match;
import dev.salonce.discordquizbot.application.MatchService;
import dev.salonce.discordquizbot.domain.Player;
import dev.salonce.discordquizbot.domain.Question;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

    private String getUsersAnswers(Match match){
        List<Question> questions = match.getQuestions();
        int currentQuestionNum = match.getCurrentQuestionNum();
        int currentQuestionCorrectAnswer = match.getCurrentQuestion().getCorrectAnswerInt();
        Map<Long, Player> players = match.getPlayers();

        List<List<String>> playersAnswers = new ArrayList<>();
        for (int i = 0; i < questions.get(currentQuestionNum).getQuizOptions().size() + 1; i++){
            playersAnswers.add(new ArrayList<>());
        }
        for (Map.Entry<Long, Player> entry : players.entrySet()){
            int intAnswer = entry.getValue().getAnswersList().get(currentQuestionNum) + 1;
            playersAnswers.get(intAnswer).add("<@" + entry.getKey().toString() + ">");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < playersAnswers.size(); i++){
            if (i != 1) sb.append("\n");
            if (currentQuestionCorrectAnswer == i - 1)
                sb.append("✅ ").append("**").append((char)('A' + i - 1)).append("**: ");
            else
                sb.append("❌ ").append((char)('A' + i - 1)).append(": ");
            for (int j = 0; j < playersAnswers.get(i).size(); j++){
                if (j != 0) sb.append(", ");
                sb.append(playersAnswers.get(i).get(j));
            }
        }
        sb.append("\n");
        sb.append("\n\uD83D\uDCA4 ").append(": ");
        for (int j = 0; j < playersAnswers.get(0).size(); j++){
            if (j != 0) sb.append(", ");
            sb.append(playersAnswers.get(0).get(j));
        }
        return sb.toString();
    }

    private String getScoreboard(Match match){
        return match.getPlayers().entrySet().stream().sorted((a, b) -> (b.getValue().getPoints() - a.getValue().getPoints())).map(entry -> "<@" + entry.getKey() + ">" + ": " + entry.getValue().getPoints() + " points").collect(Collectors.joining("\n"));
    }
}
