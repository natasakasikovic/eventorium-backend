package com.iss.eventorium.user.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@Setter
@Entity
@Table(name="roles")
public class Role implements GrantedAuthority {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name="name")
    @Getter
    String name;

    @JsonIgnore
    @Override
    public String getAuthority() {
        return name;
    }

    @JsonIgnore
    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }

}

