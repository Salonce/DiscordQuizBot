package dev.salonce.discordquizbot.core.sendingmessages;

import dev.salonce.discordquizbot.core.matches.Match;
import dev.salonce.discordquizbot.core.matches.MatchService;
import dev.salonce.discordquizbot.core.matches.Player;
import dev.salonce.discordquizbot.core.questions.questions.Question;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class QuestionMessage {

    private final MatchService matchService;

    public Mono<Message> create(MessageChannel messageChannel, Long questionNumber, int timeLeft){
        Match match = matchService.get(messageChannel);
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
                //.description("**" + match.getQuestion().getQuestion() + "**")
                .addField("\n", questionsAnswers + "\n", false)
                .addField("\n", "```⏳ " + timeLeft + " seconds left.```", false)
                .build();

        MessageCreateSpec spec = MessageCreateSpec.builder()
                .addComponent(ActionRow.of(buttons))
                .addEmbed(embed)
                .build();

        return messageChannel.createMessage(spec);
    }

    public Mono<Message> editWithTime(MessageChannel messageChannel, Message message, Long questionNumber, int timeLeft){
        Match match = matchService.get(messageChannel);
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

        return message.edit(MessageEditSpec.builder()
                .addComponent(ActionRow.of(buttons))
                .addEmbed(embed)
                .build());
    }

    public Mono<Message> editAfterAnswersWait(MessageChannel messageChannel, Message message, Long questionNumber){
        Match match = matchService.get(messageChannel);
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

        return message.edit(MessageEditSpec.builder()
                .addComponent(ActionRow.of(buttons))
                .addEmbed(embed)
                .build());
    }

    public Mono<Message> editWithScores(MessageChannel messageChannel, Message message, Long questionNumber){
        Match match = matchService.get(messageChannel);
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

        return message.edit(MessageEditSpec.builder()
                .addComponent(ActionRow.of(buttons))
                .addEmbed(embed)
                .build());
    }

    private String titleString(Match match){
        return "Question " + (match.getCurrentQuestionNum() + 1) + "/10";
    }

//    public Mono<Message> editWithScoresAndTimeLeft(MessageChannel messageChannel, Message message, Long questionNumber, Long timeLeft){
//        Match match = matchStore.get(messageChannel);
//        String questionsAnswers = match.getCurrentQuestion().getOptionsRevealed();
//        int answersSize = match.getCurrentQuestion().getQuizOptions().size();
//
//        List<Button> buttons = new ArrayList<>();
//        for (int i = 0; i < answersSize; i++) {
//            buttons.add(Button.success("Answer-" + (char)('A' + i) + "-" + questionNumber.toString(), String.valueOf((char)('A' + i))).disabled());
//            //System.out.println("Creating button of id:" + "Answer-" + (char)('A' + i) + "-" + questionNumber.toString());
//        }
//        buttons.add(Button.danger("cancelQuiz", "Abort quiz").disabled());
//
//        EmbedCreateSpec embed = EmbedCreateSpec.builder()
//                .title("Question " + (match.getCurrentQuestionNum() + 1) + "/10")
//                .addField("\n", "**" + match.getCurrentQuestion().getQuestion() + "**", false)
//                .addField("\n", questionsAnswers + "\n", false)
//                .addField("Explanation", match.getCurrentQuestion().getExplanation() + "\n", false)
//                //.addField("", "Answers:\n" + match.getUsersAnswers(), false)
//                .addField("Answers", getUsersAnswers(match), false)
//                .addField("Scoreboard", getScoreboard(match), false)
//                .addField("", "```⏳ " + timeLeft + " seconds to the next question.``` ", false)
//                .build();
//
//        return message.edit(MessageEditSpec.builder()
//                .addComponent(ActionRow.of(buttons))
//                .addEmbed(embed)
//                .build());
//    }

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
            //int intAnswer = entry.getValue().getCurrentAnswerNum() + 1;
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

    //sorted highest to lowest scores -> b - a
    private String getScoreboard(Match match){
        return match.getPlayers().entrySet().stream().sorted((a, b) -> (b.getValue().getPoints() - a.getValue().getPoints())).map(entry -> "<@" + entry.getKey() + ">" + ": " + entry.getValue().getPoints() + " points").collect(Collectors.joining("\n"));
    }
}
