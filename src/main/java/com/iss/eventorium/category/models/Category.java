package com.iss.eventorium.category.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categories")
@SQLRestriction("deleted = false")
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Size(max = 75)
    private String name;

    @Column(nullable = false)
    @Size(max = 750)
    private String description;

    private boolean suggested;

    private boolean deleted;
}
