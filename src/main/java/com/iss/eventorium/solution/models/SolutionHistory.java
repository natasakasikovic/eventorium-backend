package com.iss.eventorium.solution.models;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "solutions_history")
public class SolutionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Memento> productHistories = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<Memento> serviceHistories = new ArrayList<>();

    public void addServiceMemento(Memento memento) {
        addSolutionMemento(memento, serviceHistories);
    }
    public void addProductMemento(Memento memento) {
        addSolutionMemento(memento, productHistories);
    }

    private void addSolutionMemento(Memento memento, List<Memento> productHistories) {
        productHistories.add(memento);
        if(productHistories.size() > 1) {
            for(int i = productHistories.size() - 2; i >= 0; i--) {
                if(productHistories.get(i).getSolutionId().equals(memento.getSolutionId())) {
                    productHistories.get(i).setValidTo(LocalDateTime.now());
                }
            }
        }
    }
}
