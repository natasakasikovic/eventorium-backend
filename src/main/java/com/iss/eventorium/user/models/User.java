package com.iss.eventorium.user.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "profile_picture", nullable = true)
    private String profilePicture;

    @Column(name = "activated", nullable = false)
    private boolean activated;

    @Column(name = "activation_timestamp", nullable = false)
    private Date activationTimestamp;

    @Column(name = "suspended", nullable = false)
    private boolean suspended;

    @Embedded
    private Person person;

    @Transient
    private String jwt;
}
