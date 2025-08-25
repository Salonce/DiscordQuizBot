package dev.salonce.discordquizbot.domain;

import java.util.ArrayList;
import java.util.List;

public class AnswerDistribution {

    List<AnswerOptionGroup> answerOptionGroups = new ArrayList<>();

    public AnswerDistribution(List<AnswerOptionGroup> answerOptionGroups){
        this.answerOptionGroups = answerOptionGroups;
    }

}
