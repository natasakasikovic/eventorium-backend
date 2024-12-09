package com.iss.eventorium.security.jwt;

import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService jwtUserDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getRequestURL().toString().contains("/api/")) {

            String requestTokenHeader = request.getHeader("Authorization");

            // NOTE: 'email' is treated as 'username' in this section to align with Spring Security's conventions.
            String username = null;
            String jwtToken = null;

            if (requestTokenHeader != null && requestTokenHeader.contains("Bearer")) {
                jwtToken = requestTokenHeader.substring(requestTokenHeader.indexOf("Bearer ") + 7);
                try {
                    username = jwtTokenUtil.extractUsername(jwtToken);
                    UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);
                    if (jwtTokenUtil.validateToken(jwtToken, username)) {
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        usernamePasswordAuthenticationToken
                                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                } catch (IllegalArgumentException e) {
                    logger.warn("Unable to get JWT Token.");
                } catch(TokenExpiredException ex) {
                    logger.error("JWT Token expired!");
                }
            } else {
                logger.warn("JWT Token does not exist.");
            }
        }
        filterChain.doFilter(request, response);

    }
}
