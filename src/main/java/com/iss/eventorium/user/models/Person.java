package com.iss.eventorium.user.models;

import com.iss.eventorium.shared.models.City;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String lastname;

    private String address;

    private String phoneNumber;

    @ManyToOne
    @JoinColumn(name = "city_id", referencedColumnName = "id")
    private City city;

    @Column(name = "profile_photo")
    private String profilePhoto;
}
