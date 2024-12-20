package com.iss.eventorium.user.models;

import com.iss.eventorium.shared.models.City;
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

    @Column
    private String name;

    @Column
    private String lastname;

    @Column
    private String address;

    @Column
    private String phoneNumber;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "city_id", referencedColumnName = "id")
    private City city;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Product> favouriteProducts;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Service> favouriteServices;
}
