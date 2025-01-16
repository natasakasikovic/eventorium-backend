package com.iss.eventorium.security.auth;

import com.iss.eventorium.security.utils.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String username;
        String authToken = jwtTokenUtil.getToken(request);

        try {

            if (authToken != null && !authToken.isEmpty()) {
                username = jwtTokenUtil.getUsernameFromToken(authToken);

                if (username != null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                        TokenBasedAuthentication authentication = new TokenBasedAuthentication(userDetails);
                        authentication.setToken(authToken);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        }
        catch (ExpiredJwtException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token expired\"}");
            return;
        }
        chain.doFilter(request, response);
    }

}
