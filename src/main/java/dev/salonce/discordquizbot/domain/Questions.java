package dev.salonce.discordquizbot.domain;

import java.util.List;

public class Questions {
    private final List<Question> list;
    private int currentIndex = 0;

    public Questions(List<Question> questions) {
        if (questions == null || questions.isEmpty()) {
            throw new IllegalArgumentException("Questions cannot be empty.");
        }
        this.list = List.copyOf(questions); // make immutable
    }

    public Question current() {
        return exists() ? list.get(currentIndex) : null;
    }

    public void next() {
        if (exists()) currentIndex++;
    }

    public boolean exists() {
        return currentIndex < list.size();
    }

    public int size() {
        return list.size();
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public Question get(int index) {
        return list.get(index);
    }
}
