package com.iss.eventorium.solution.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "mementos")
public class Memento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Solution solution;

    private String name;
    private double price;
    private double discount;

    private LocalDateTime validFrom;
    private LocalDateTime validTo;
}
