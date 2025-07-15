package com.iss.eventorium.company.models;

import com.iss.eventorium.shared.models.City;
import com.iss.eventorium.shared.models.ImagePath;
import com.iss.eventorium.shared.utils.ImageHolder;
import com.iss.eventorium.user.models.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "companies")
public class Company implements ImageHolder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @ManyToOne
    @JoinColumn(name = "city_id", referencedColumnName = "id")
    private City city;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false, length = 1024)
    private String description;

    @Column(nullable = false)
    private String email;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ImagePath> imagePaths;

    @Column(nullable = false)
    private LocalTime openingHours;

    @Column(nullable = false)
    private LocalTime closingHours;

    @OneToOne(fetch = FetchType.LAZY)
    private User provider;
}
