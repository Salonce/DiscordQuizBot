package dev.salonce.discordQuizBot.Core.Matches;

import dev.salonce.discordQuizBot.Core.Questions.Question;
import discord4j.core.object.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class Match{

    private final Map<User, Player> players;
    private final List<Question> questions;
    private int questionNumber;
    @Setter
    private boolean enrolment;

    public String getUsersAnswers(){
        List<List<String>> playersAnswers = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++){
            playersAnswers.add(new ArrayList<>());
        }
        for (Map.Entry<User, Player> entry : players.entrySet()){
            int intAnswer = entry.getValue().getCurrentAnswerNum();
            playersAnswers.get(intAnswer).add(entry.getKey().getUsername());
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < playersAnswers.size(); i++){
            if (i != 0) sb.append("\n");
            sb.append((char)('A' + i)).append(": ");
            for (int j = 0; j < playersAnswers.get(i).size(); j++){
                if (j != 0) sb.append(", ");
                sb.append(playersAnswers.get(i).get(j));
            }
        }
        return sb.toString();
    }

    public Question getQuestion(){
        if (questionNumber <= questions.size())
            return questions.get(questionNumber);
        else
            return null;
    }

    public boolean nextQuestion(){
        if (++questionNumber <= questions.size() - 1)
            return true;
        else
            return false;
    }

    public String getUserNames() {
        return players.keySet().stream()
                .map(User::getUsername)
                .collect(Collectors.joining(", "));
    }

    public Match(List<Question> questions){
        this.questions = questions;
        this.players = new HashMap<>();
        this.enrolment = true;
        this.questionNumber = 0;
    }

    public boolean addPlayer(User user){
        if (players.containsKey(user)) {
            //System.out.println("User is already on player list.");
            return false;
        }
        else {
            players.put(user, new Player());
            return true;
        }
    }

    public boolean removePlayer(User user){
        if (players.containsKey(user)) {
            players.remove(user);
            return true;
        }
        else {
            //System.out.println("User is not on the player list.");
            return false;
        }
    }


}
