package com.iss.eventorium.solution.models;

import jakarta.persistence.*;
import lombok.Getter;

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
        serviceHistories.add(memento);
        if(serviceHistories.size() > 1) {
            serviceHistories.get(serviceHistories.size() - 2).setValidTo(memento.getValidFrom());
        }
    }
    public void addProductMemento(Memento memento) {
        productHistories.add(memento);
        if(productHistories.size() > 1) {
            productHistories.get(productHistories.size() - 2).setValidTo(memento.getValidFrom());
        }
    }
}
