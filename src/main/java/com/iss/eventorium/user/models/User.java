package com.iss.eventorium.user.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String email;
    private String password;
    private Role role;
    private boolean activated;
    private Date activationTimestamp;
    private boolean suspended;
    private Person person;
}
