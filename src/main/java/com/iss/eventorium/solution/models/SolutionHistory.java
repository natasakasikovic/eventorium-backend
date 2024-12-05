package com.iss.eventorium.solution.models;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SolutionHistory {
    private final List<SolutionMemento> histories = new ArrayList<>();

    public void addMemento(SolutionMemento memento) {
        histories.add(memento);
    }
}
