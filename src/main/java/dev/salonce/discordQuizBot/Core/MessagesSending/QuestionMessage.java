package dev.salonce.discordQuizBot.Core.MessagesSending;

import dev.salonce.discordQuizBot.Core.Matches.Match;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuestionMessage {

    private final Map<MessageChannel, Match> matches;

    public QuestionMessage(Map<MessageChannel, Match> matches){
        this.matches = matches;
    }

    public Mono<Message> createQuestionMessage(MessageChannel messageChannel, Long questionNumber, int timeLeft){
        Match match = matches.get(messageChannel);
        String questionsAnswers = match.getCurrentQuestion().getStringAnswers(false);
        int answersSize = match.getCurrentQuestion().getAnswers().size();

        List<Button> buttons = new ArrayList<>();
        for (int i = 0; i < answersSize; i++) {
            buttons.add(Button.success("Answer-" + (char)('A' + i) + "-" + questionNumber.toString(), String.valueOf((char)('A' + i))));
            //System.out.println("Creating button of id:" + "Answer-" + (char)('A' + i) + "-" + questionNumber.toString());
        }
        buttons.add(Button.danger("cancelQuiz", "Abort quiz"));

        //String formattedTime = String.format("%02d", timeLeft);

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("Question " + (match.getCurrentQuestionNum() + 1) + "/10")
                .addField("\n", "**" + match.getCurrentQuestion().getQuestion() + "**", false)
                //.description("**" + match.getQuestion().getQuestion() + "**")
                .addField("\n", questionsAnswers + "\n", false)
                .addField("\n", "```" + timeLeft + " seconds left.```", false)
                .build();

        MessageCreateSpec spec = MessageCreateSpec.builder()
                .addComponent(ActionRow.of(buttons))
                .addEmbed(embed)
                .build();

        return messageChannel.createMessage(spec);
    }

    public Mono<Message> editQuestionMessageInitial(MessageChannel messageChannel, Message message, Long questionNumber){
        Match match = matches.get(messageChannel);
        String questionsAnswers = match.getCurrentQuestion().getStringAnswers(false);
        int answersSize = match.getCurrentQuestion().getAnswers().size();

        List<Button> buttons = new ArrayList<>();
        for (int i = 0; i < answersSize; i++) {
            buttons.add(Button.success("Answer-" + (char)('A' + i) + "-" + questionNumber.toString(), String.valueOf((char)('A' + i))).disabled());
            //System.out.println("Creating button of id:" + "Answer-" + (char)('A' + i) + "-" + questionNumber.toString());
        }
        buttons.add(Button.danger("cancelQuiz", "Abort quiz"));

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("Question " + (match.getCurrentQuestionNum() + 1) + "/10")
                .addField("\n", "**" + match.getCurrentQuestion().getQuestion() + "**", false)
                //.description("**" + match.getQuestion().getQuestion() + "**")
                .addField("\n", questionsAnswers + "\n", false)
                .build();

        return message.edit(MessageEditSpec.builder()
                .addComponent(ActionRow.of(buttons))
                .addEmbed(embed)
                .build());
    }

    public Mono<Message> editQuestionMessageTime(MessageChannel messageChannel, Message message, Long questionNumber, int timeLeft){
        Match match = matches.get(messageChannel);
        String questionsAnswers = match.getCurrentQuestion().getStringAnswers(false);
        int answersSize = match.getCurrentQuestion().getAnswers().size();

        List<Button> buttons = new ArrayList<>();
        for (int i = 0; i < answersSize; i++) {
            buttons.add(Button.success("Answer-" + (char)('A' + i) + "-" + questionNumber.toString(), String.valueOf((char)('A' + i))));
            //System.out.println("Creating button of id:" + "Answer-" + (char)('A' + i) + "-" + questionNumber.toString());
        }
        buttons.add(Button.danger("cancelQuiz", "Abort quiz"));

        //String formattedTime = String.format("%02d", timeLeft);

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("Question " + (match.getCurrentQuestionNum() + 1) + "/10")
                .addField("\n", "**" + match.getCurrentQuestion().getQuestion() + "**", false)
                //.description("**" + match.getQuestion().getQuestion() + "**")
                .addField("\n", questionsAnswers + "\n", false)
                .addField("\n", "```" + timeLeft + " seconds left.```", false)
                //.footer("Question " + questionNumber + " out of 10", null)
                .build();

        return message.edit(MessageEditSpec.builder()
                .addComponent(ActionRow.of(buttons))
                .addEmbed(embed)
                .build());
    }

    public Mono<Message> editQuestionMessage(MessageChannel messageChannel, Message message, Long questionNumber){
        Match match = matches.get(messageChannel);
        String questionsAnswers = match.getCurrentQuestion().getStringAnswers(true);
        int answersSize = match.getCurrentQuestion().getAnswers().size();

        List<Button> buttons = new ArrayList<>();
        for (int i = 0; i < answersSize; i++) {
            buttons.add(Button.success("Answer-" + (char)('A' + i) + "-" + questionNumber.toString(), String.valueOf((char)('A' + i))).disabled());
            //System.out.println("Creating button of id:" + "Answer-" + (char)('A' + i) + "-" + questionNumber.toString());
        }
        buttons.add(Button.danger("cancelQuiz", "Abort quiz").disabled());

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("Question " + (match.getCurrentQuestionNum() + 1) + "/10")
                .addField("\n", "**" + match.getCurrentQuestion().getQuestion() + "**", false)
                .addField("\n", questionsAnswers + "\n", false)
                .addField("Explanation", match.getCurrentQuestion().getExplanation() + "\n", false)
                //.addField("", "Answers:\n" + match.getUsersAnswers(), false)
                .addField("Answers", match.getUsersAnswers(), false)
                .addField("Scoreboard", match.getScoreboard(), false)
                .build();

        return message.edit(MessageEditSpec.builder()
                .addComponent(ActionRow.of(buttons))
                .addEmbed(embed)
                .build());
    }

}
