package dev.salonce.discordQuizBot;

import dev.salonce.discordQuizBot.Core.Matches.EnumMatchState;
import dev.salonce.discordQuizBot.Core.Matches.Player;
import dev.salonce.discordQuizBot.Core.Questions.Question;
import discord4j.core.object.entity.User;

import java.util.List;
import java.util.Map;

public interface MatchI {
    void addPlayer(User user); // plyaer -> user?
    void removePlayer(User user); // player -> user?

    Map<User, Player> getUserPlayerMap();

    List<Question> getQuestions();
    boolean hasNextQuestion();
    Question nextQuestion();

    void refreshUnansweredQuestionsInARowCount();
    void addToUnansweredQuestionsInARowCount();

    void updatePoints();

    EnumMatchState getMatchClosed();
    boolean everyoneAnswered();
    boolean isClosed();

    String getScoreBoard();
    String getWinners();
    String getPlayersAnswers();
    String getUsernames();



}
