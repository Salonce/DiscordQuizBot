package dev.salonce.discordQuizBot;

import dev.salonce.discordQuizBot.Configs.QuestionSetsConfig;
import dev.salonce.discordQuizBot.Buttons.AnswerInteractionEnum;
import dev.salonce.discordQuizBot.Buttons.ButtonInteraction;
import dev.salonce.discordQuizBot.Buttons.ButtonInteractionData;
import dev.salonce.discordQuizBot.Configs.QuizConfig;
import dev.salonce.discordQuizBot.Core.Matches.Match;
import dev.salonce.discordQuizBot.Core.Matches.EnumMatchClosed;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizManager {

    private final QuizConfig quizConfig;

    private final Map<MessageChannel, Match> quizzes;
    private final QuestionSetsConfig questionSetsConfig;

    public QuizManager(QuestionSetsConfig questionSetsConfig, QuizConfig quizConfig){
        quizzes = new HashMap<>();
        this.questionSetsConfig = questionSetsConfig;
        this.quizConfig = quizConfig;
    }

public void addMatch(MessageChannel messageChannel, Match match) {
    int totalTimeToJoinLeft = quizConfig.getTimeToJoinQuiz();
    int totalTimeToStartLeft = quizConfig.getTimeToStartMatch();

    if (quizzes.containsKey(messageChannel)) {
        //send message that starting a match is impossible because there is already one
    } else {
        quizzes.put(messageChannel, match);

        createStartQuizMessage(messageChannel, totalTimeToJoinLeft)
                .flatMap(message ->
                        Flux.interval(Duration.ofSeconds(1))
                                .take(totalTimeToJoinLeft)
                                .takeUntil(interval -> match.isClosed()) // Stop if match is closed
                                .takeUntil(interval -> match.isStartNow())
                                .flatMap(interval -> {
                                    Long timeLeft = (long) (totalTimeToJoinLeft - interval.intValue() - 1);
                                    return editStartQuizMessage(message, messageChannel, timeLeft);
                                })
                                .then(Mono.just(message))
                )
                .flatMap(message -> closeEnrollment(message, match))
                .flatMap(message ->
                        Mono.defer(() -> {
                            if (match.isClosed()) {
                                return Mono.just(message); // Skip to next stage if closed
                            }
                            return Flux.interval(Duration.ofSeconds(1))
                                    .take(totalTimeToStartLeft + 1)
                                    .takeUntil(interval -> match.isClosed())
                                    .flatMap(interval -> {
                                        Long timeLeft = (long) (totalTimeToStartLeft - interval.intValue());
                                        return editStartQuizMessage2(message, messageChannel, timeLeft);
                                    })
                                    .then(Mono.just(message));
                        })
                )
                .flatMap(message ->
                        Mono.defer(() -> {
                            if (match.isClosed()) {
                                return Mono.just(message); // Skip question messages if closed
                            }
                            System.out.println("before it creates q messages");
                            return createQuestionMessages(messageChannel);
                        })
                )
                .then(Mono.defer(() -> {
                            if (match.isClosed()) {
                                System.out.println("createCanceledMatchMessage");
                                return createCanceledMatchMessage(messageChannel);
                            }
                            System.out.println("createMatchResultsMsg");
                            return createMatchResultsMessage(messageChannel);
                        })
                )
                .then(Mono.defer(() -> Mono.just(quizzes.remove(messageChannel))))
                .subscribe();
    }
}

    private Mono<Message> closeEnrollment(Message monoMessage, Match match){
        match.setEnrollment(false);
        System.out.println("enrollment closed");
        return Mono.just(monoMessage);
    }

    private Mono<Void> createQuestionMessagesSequentially(MessageChannel messageChannel) {
        Match match = quizzes.get(messageChannel);

        return Flux.generate(sink -> {
                    if (match.questionExists())
                        sink.next(match.getQuestion());
                    else
                        sink.complete();
                })
                .takeWhile(question -> !match.isClosed())
                .index()
                .concatMap(tuple -> {
                            long index = tuple.getT1();
                            return createQuestionMessage(messageChannel, index, quizConfig.getTimeToPickAnswer())
                                    .flatMap(message -> {
                                        openAnswering(messageChannel);
                                        int totalTime = quizConfig.getTimeToPickAnswer();
                                        return Flux.interval(Duration.ofSeconds(1)) // Emit every second
                                                .take(totalTime)// Number of updates
                                                .takeUntil(interval -> match.isClosed() || match.everyoneAnswered())
                                                .flatMap(interval -> {
                                                    int timeLeft = totalTime - (interval.intValue() + 1); // Calculate remaining time
                                                    return editQuestionMessageTime(messageChannel, message, index, timeLeft);
                                                })
                                                //.then(Mono.defer(() -> openAnswering(messageChannel)))
                                                //.then(Mono.delay(Duration.ofSeconds(totalTime)))
                                                .then(Mono.defer(() -> editQuestionMessageInitial(messageChannel, message, index)))
                                                .then(Mono.delay(Duration.ofSeconds(1)))
                                                .then(Mono.defer(() -> addPlayerPoints(messageChannel)))
                                                .then(Mono.defer(() -> closeAnswering(messageChannel)))
                                                .then(Mono.defer(() -> editQuestionMessage(messageChannel, message, index)))
                                                .then(Mono.defer(() -> setNoAnswerCountAndCloseMatchIfLimit(messageChannel)))
                                                .then(Mono.delay(Duration.ofSeconds(quizConfig.getTimeForNewQuestionToAppear())))
                                                .then(Mono.defer(() -> moveToNextQuestion(match)));
                                    });
                        }
                )
                .then();
    }

    private Mono<Void> setNoAnswerCountAndCloseMatchIfLimit(MessageChannel messageChannel){
        Match match = quizzes.get(messageChannel);
        match.setNoAnswerCountAndCloseMatchIfLimit();
        return Mono.empty();
    }

    private Mono<Void> closeAnswering(MessageChannel messageChannel){
        Match match = quizzes.get(messageChannel);
        match.setAnsweringOpen(false);
        return Mono.empty();
    }

    private Mono<Void> openAnswering(MessageChannel messageChannel){
        Match match = quizzes.get(messageChannel);
        match.setAnsweringOpen(true);
        return Mono.empty();
    }

    private Mono<Message> createQuestionMessage(MessageChannel messageChannel, Long questionNumber, int timeLeft){
        Match match = quizzes.get(messageChannel);
        String questionsAnswers = match.getQuestion().getStringAnswers(false);
        int answersSize = match.getQuestion().getAnswers().size();

        List<Button> buttons = new ArrayList<>();
        for (int i = 0; i < answersSize; i++) {
            buttons.add(Button.success("Answer-" + (char)('A' + i) + "-" + questionNumber.toString(), String.valueOf((char)('A' + i))));
            //System.out.println("Creating button of id:" + "Answer-" + (char)('A' + i) + "-" + questionNumber.toString());
        }
        buttons.add(Button.danger("cancelQuiz", "Abort quiz"));

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("#" + (match.getCurrentQuestionNum() + 1) + " **" + match.getQuestion().getQuestion() + "**")
                //.description("**" + match.getQuestion().getQuestion() + "**")
                .addField("\n", questionsAnswers + "\n", false)
                .addField("\n", "You have " + timeLeft + "s to answer." + "\n", false)
                .build();

        MessageCreateSpec spec = MessageCreateSpec.builder()
                .addComponent(ActionRow.of(buttons))
                .addEmbed(embed)
                .build();

        return messageChannel.createMessage(spec);
    }

    private Mono<Message> editQuestionMessageTime(MessageChannel messageChannel, Message message, Long questionNumber, int timeLeft){
        Match match = quizzes.get(messageChannel);
        String questionsAnswers = match.getQuestion().getStringAnswers(false);
        int answersSize = match.getQuestion().getAnswers().size();

        List<Button> buttons = new ArrayList<>();
        for (int i = 0; i < answersSize; i++) {
            buttons.add(Button.success("Answer-" + (char)('A' + i) + "-" + questionNumber.toString(), String.valueOf((char)('A' + i))));
            //System.out.println("Creating button of id:" + "Answer-" + (char)('A' + i) + "-" + questionNumber.toString());
        }
        buttons.add(Button.danger("cancelQuiz", "Abort quiz"));

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("#" + (match.getCurrentQuestionNum() + 1) + " **" + match.getQuestion().getQuestion() + "**")
                //.description("**" + match.getQuestion().getQuestion() + "**")
                .addField("\n", questionsAnswers + "\n", false)
                .addField("\n", "You have " + timeLeft + "s to answer." + "\n", false)
                .build();

        return message.edit(MessageEditSpec.builder()
                .addComponent(ActionRow.of(buttons))
                .addEmbed(embed)
                .build());
    }

    private Mono<Message> editQuestionMessageInitial(MessageChannel messageChannel, Message message, Long questionNumber){
        Match match = quizzes.get(messageChannel);
        String questionsAnswers = match.getQuestion().getStringAnswers(false);
        int answersSize = match.getQuestion().getAnswers().size();

        List<Button> buttons = new ArrayList<>();
        for (int i = 0; i < answersSize; i++) {
            buttons.add(Button.success("Answer-" + (char)('A' + i) + "-" + questionNumber.toString(), String.valueOf((char)('A' + i))).disabled());
            //System.out.println("Creating button of id:" + "Answer-" + (char)('A' + i) + "-" + questionNumber.toString());
        }
        buttons.add(Button.danger("cancelQuiz", "Abort quiz"));

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("#" + (match.getCurrentQuestionNum() + 1) + " **" + match.getQuestion().getQuestion() + "**")
                //.description("**" + match.getQuestion().getQuestion() + "**")
                .addField("\n", questionsAnswers + "\n", false)
                .build();

        return message.edit(MessageEditSpec.builder()
                .addComponent(ActionRow.of(buttons))
                .addEmbed(embed)
                .build());
    }


    private Mono<Message> editQuestionMessage(MessageChannel messageChannel, Message message, Long questionNumber){
        Match match = quizzes.get(messageChannel);
        String questionsAnswers = match.getQuestion().getStringAnswers(true);
        int answersSize = match.getQuestion().getAnswers().size();

        List<Button> buttons = new ArrayList<>();
        for (int i = 0; i < answersSize; i++) {
            buttons.add(Button.success("Answer-" + (char)('A' + i) + "-" + questionNumber.toString(), String.valueOf((char)('A' + i))).disabled());
            //System.out.println("Creating button of id:" + "Answer-" + (char)('A' + i) + "-" + questionNumber.toString());
        }
        buttons.add(Button.danger("cancelQuiz", "Abort quiz").disabled());

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("#" + (match.getCurrentQuestionNum() + 1) + " **" + match.getQuestion().getQuestion() + "**")
                .addField("\n", questionsAnswers + "\n", false)
                .addField("Explanation", match.getQuestion().getExplanation() + "\n", false)
                //.addField("", "Answers:\n" + match.getUsersAnswers(), false)
                .addField("Answers", match.getUsersAnswers(), false)
                .addField("Scoreboard", match.getScoreboard(), false)
                .build();

        return message.edit(MessageEditSpec.builder()
                .addComponent(ActionRow.of(buttons))
                .addEmbed(embed)
                .build());
    }

    public Mono<Message> createStartQuizMessage(MessageChannel messageChannel, int timeToJoinLeft){
        Match match = quizzes.get(messageChannel);

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                //.title("\uD83C\uDFC1 Java Quiz")
                .title("\uD83C\uDFC1" + match.getName() + " quiz")
                .addField("Number of questions", String.valueOf(match.getQuestions().size()), false)
                .addField("Participants", match.getUserNames(), false)
                .addField("Time", timeToJoinLeft + " seconds to join.", false)
                .build();

        MessageCreateSpec spec = MessageCreateSpec.builder()
                .addComponent(ActionRow.of(Button.primary("startNow", "Start now"), Button.success("joinQuiz", "Join"), Button.success("leaveQuiz", "Leave"), Button.danger("cancelQuiz", "Cancel")))
                .addEmbed(embed)
                .build();

        return messageChannel.createMessage(spec);
    }

    private Mono<Message> editStartQuizMessage(Message message, MessageChannel messageChannel, Long timeToJoinLeft){
        Match match = quizzes.get(messageChannel);

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                //.title("\uD83C\uDFC1 Java Quiz")
                .title("\uD83C\uDFC1" + match.getName() + " quiz")
                .addField("Number of questions", String.valueOf(match.getQuestions().size()), false)
                .addField("Participants", match.getUserNames(), false)
                .addField("Time", timeToJoinLeft + " seconds to join.", false)
                .build();

        return message.edit(MessageEditSpec.builder()
                .addComponent(ActionRow.of(Button.primary("startNow", "Start now"), Button.success("joinQuiz", "Join"), Button.success("leaveQuiz", "Leave"), Button.danger("cancelQuiz", "Cancel")))
                .addEmbed(embed)
                .build());
    }

    private Mono<Message> editStartQuizMessage2(Message message, MessageChannel messageChannel, Long timeToStartLeft){
        Match match = quizzes.get(messageChannel);

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                //.title("\uD83C\uDFC1 Java Quiz")
                .title("\uD83C\uDFC1" + match.getName() + " quiz")
                .addField("Number of questions", String.valueOf(match.getQuestions().size()), false)
                .addField("Participants", match.getUserNames(), false)
                .addField("Time", timeToStartLeft + " seconds to start.", false)
                .build();

        return message.edit(MessageEditSpec.builder()
                .addComponent(ActionRow.of(Button.primary("startNow", "Start now").disabled(), Button.success("joinQuiz", "Join").disabled(), Button.success("leaveQuiz", "Leave").disabled(), Button.danger("cancelQuiz", "Cancel").disabled()))
                .addEmbed(embed)
                .build());
    }

    private Mono<Void> createQuestionMessages(MessageChannel messageChannel) {
        return createQuestionMessagesSequentially(messageChannel); // Process all questions sequentially
                //.then(Mono.delay(Duration.ofSeconds(2))) // add time after last question?

    }

    private Mono<Void> moveToNextQuestion(Match match){
        match.nextQuestion();
        return Mono.empty();
    }

    private Mono<Message> createMatchResultsMessage(MessageChannel messageChannel){
        Match match = quizzes.get(messageChannel);

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title("Final scoreboard: " )
                .description(match.getScoreboard())
                .addField("\uD83C\uDFC6", "The winners are: " + match.getWinners(), false)
                .build();

        return messageChannel.createMessage(embed);
    }

    private Mono<Message> createCanceledMatchMessage(MessageChannel messageChannel){
        Match match = quizzes.get(messageChannel);
        String text = "Match has been closed.";
        if (match.getEnumMatchClosed() == EnumMatchClosed.BY_AUTOCLOSE)
            text = "Match has been autoclosed.";
        else if (match.getEnumMatchClosed() == EnumMatchClosed.BY_OWNER)
            text = "Match has been closed by the owner.";

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .title(text )
                .build();

        return messageChannel.createMessage(embed);
    }

    public boolean cancelQuiz(ButtonInteraction buttonInteraction) {
        User user = buttonInteraction.getUser();
//      Message message = buttonInteraction.getMessage();
        MessageChannel messageChannel = buttonInteraction.getMessageChannel();
        Match match = quizzes.get(messageChannel);
        return match.closeMatch(user.getId().asLong());
    }

    public String addUserToMatch(ButtonInteraction buttonInteraction){
        User user = buttonInteraction.getUser();
        Message message = buttonInteraction.getMessage();
        MessageChannel messageChannel = buttonInteraction.getMessageChannel();
        Match match = quizzes.get(messageChannel);
        int questionsNumber = match.getQuestions().size();

        if (quizzes.containsKey(messageChannel)){
            return quizzes.get(messageChannel).addPlayer(user, questionsNumber);
        }
        else{
            return "This match doesn't exist anymore.";
        }
    }

    public String removeUserFromMatch(ButtonInteraction buttonInteraction){
        User user = buttonInteraction.getUser();
        Message message = buttonInteraction.getMessage();
        MessageChannel messageChannel = buttonInteraction.getMessageChannel();

        if (quizzes.containsKey(messageChannel))
            return quizzes.get(messageChannel).removePlayer(user);
        else{
            return "This match doesn't exist anymore.";
        }
    }

    public String startNow(ButtonInteraction buttonInteraction){
        User user = buttonInteraction.getUser();
        Message message = buttonInteraction.getMessage();
        MessageChannel messageChannel = buttonInteraction.getMessageChannel();

        if (quizzes.containsKey(messageChannel)){
            if (!quizzes.get(messageChannel).isStartNow()) {
                quizzes.get(messageChannel).setStartNow(true);
                return "Starting immediately";
            }
            else
                return "Already started";
        }
        else{
            return "This match doesn't exist anymore.";
        }
    }

    public AnswerInteractionEnum setPlayerAnswer(ButtonInteraction buttonInteraction, ButtonInteractionData buttonInteractionData){
        User user = buttonInteraction.getUser();
        MessageChannel messageChannel = buttonInteraction.getMessageChannel();
        int questionNum = buttonInteractionData.getQuestionNumber();
        int answerNum = buttonInteractionData.getAnswerNumber();

        Match match = quizzes.get(messageChannel);

        if (match == null)
            return AnswerInteractionEnum.TOO_LATE; // could be different

        if (questionNum != match.getCurrentQuestionNum() || match.isAnsweringOpen() != true)
            return AnswerInteractionEnum.TOO_LATE;

        if (match.getPlayers().containsKey(user)) {
            match.getPlayers().get(user).getAnswersList().set(questionNum, answerNum);
            return AnswerInteractionEnum.VALID;
        }
        else return AnswerInteractionEnum.NOT_IN_MATCH;
    }

    public Mono<Void> addPlayerPoints(MessageChannel messageChannel){
        quizzes.get(messageChannel).addPlayerPoints();
        return Mono.empty();
    }

    public Mono<Message> sendHelpMessage(MessageChannel messageChannel) {
        Match match = quizzes.get(messageChannel);
        String example = null;
        if (!questionSetsConfig.getFiles().keySet().isEmpty())
            example = questionSetsConfig.getFiles().keySet().iterator().next();

        EmbedCreateSpec embed;
        if (example != null) {
            EmbedCreateSpec.Builder embedBuilder = EmbedCreateSpec.builder()
                    .title("Help");

            List<String> categories = questionSetsConfig.getFiles().keySet().stream().sorted(String::compareTo).toList();

            embed = embedBuilder
                    .addField("Categories", categories.stream().collect(Collectors.joining("\n")), false)
                    .addField("Example", "To start **" + example + "** quiz, type: **qq quiz " + example + "**.", false)
                    .build();
        }
        else{
            embed = EmbedCreateSpec.builder()
                    .title("No data" )
                    .addField("", "Sorry. This bot has no available quizzes.", false)
                    .build();
        }

        return messageChannel.createMessage(embed);
    }
}