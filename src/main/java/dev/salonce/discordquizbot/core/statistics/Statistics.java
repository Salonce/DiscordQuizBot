package dev.salonce.discordquizbot.core.statistics;

import dev.salonce.discordquizbot.core.matches.Match;
import org.springframework.stereotype.Component;

@Component
public class Statistics {
    private int matches_started = 0;
    //function that prints amount of guilds, their ids and names in a string on call

    public void addMatch(Match match){
        System.out.println("Starting match nr " + ++matches_started);
        System.out.println("Creator: " + "<@" + match.getOwnerId() + ">");
        System.out.println("Topic: " + match.getTopic() + " " + match.getDifficulty());
        System.out.println();

    }
    //function that increases amount of int matches whenever a match is called and prints it in console in form: Match nr X started by user Y in Guild of name Z
}
