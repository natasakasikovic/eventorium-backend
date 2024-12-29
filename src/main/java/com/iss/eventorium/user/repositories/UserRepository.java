package com.iss.eventorium.user.repositories;

import com.iss.eventorium.solution.models.Product;
import com.iss.eventorium.user.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByHash(String hash);
}
