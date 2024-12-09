package com.iss.eventorium.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.iss.eventorium.user.models.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

@Component
public class JwtTokenUtil implements Serializable {
    private static final long serialVersionUID = 1L;

    private final long EXPIRATION_TIME = 1000 * 60 * 60;
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public String generateToken(String username, Role role) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        return JWT.create()
                .withSubject(username)
                .withClaim("role", role.ordinal())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(algorithm);
    }

    public boolean isTokenExpired(String token) {
        DecodedJWT decodedJWT = decodeToken(token);
        return decodedJWT.getExpiresAt().before(new Date());
    }

    private DecodedJWT decodeToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        return JWT.require(algorithm)
                .build()
                .verify(token);
    }

    public String extractUsername(String token) {
        DecodedJWT decodedJWT = decodeToken(token);
        return decodedJWT.getSubject();
    }

    public boolean validateToken(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }
}
