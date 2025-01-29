package com.iss.eventorium.user.models;

import com.iss.eventorium.event.models.Event;
import com.iss.eventorium.shared.models.City;
import com.iss.eventorium.shared.models.ImagePath;
import com.iss.eventorium.solution.models.Product;
import com.iss.eventorium.solution.models.Service;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_photo_id", referencedColumnName = "id")
    private ImagePath profilePhoto;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Product> favouriteProducts;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Service> favouriteServices;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Event> favouriteEvents;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Event> attendingEvents;
}
