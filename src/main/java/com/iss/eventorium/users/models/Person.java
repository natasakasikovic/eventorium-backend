package com.iss.eventorium.users.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    private int id;
    private String name;
    private String lastname;
    private String address; // street and number
    private City city;
    private User account;
}
