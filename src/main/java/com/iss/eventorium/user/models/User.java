package com.iss.eventorium.user.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles;

    @Column(name = "verified", nullable = false)
    private boolean verified = false;

    @Column(name = "activation_timestamp", nullable = false)
    private Date activationTimestamp = new Date();

    @Column(name = "last_password_reset", nullable = false)
    private Date lastPasswordReset = new Date();

    @Column(name = "suspended")
    private LocalDateTime suspended = null;

    @Column(name = "deactivated")
    private boolean deactivated = false;

    @Embedded
    private Person person;

    @Transient
    private String jwt;

    @Column(unique = true)
    private String hash;

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }
    public String getUsername() {
        return email;
    }
}
