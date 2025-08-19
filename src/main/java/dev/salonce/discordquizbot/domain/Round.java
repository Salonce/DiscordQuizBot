package dev.salonce.discordquizbot.domain;

import java.util.HashMap;
import java.util.Map;

public class Round {

    int index;
    Long channelId;
    Map<Long, Answer> answers = new HashMap<>();



}
