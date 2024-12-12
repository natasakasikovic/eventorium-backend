package com.iss.eventorium.config;

import com.iss.eventorium.security.auth.JwtRequestFilter;
import com.iss.eventorium.security.auth.RestAuthenticationEntryPoint;
import com.iss.eventorium.user.services.CustomUserDetailsService;
import com.iss.eventorium.user.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity(debug = true)
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private UserService userService;
    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults());
        http.csrf((csrf) -> csrf.disable());
        http.exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(restAuthenticationEntryPoint));
        http.authorizeHttpRequests(request -> request
                        .requestMatchers(new AntPathRequestMatcher("/*")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/*/*")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/auth/login")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/error")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/users/reset-password/**")).authenticated()
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/account/**")).authenticated()
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/categories")).authenticated()
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/categories/**")).hasRole("ADMIN")
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/account/services")).hasRole("PROVIDER")
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/account/products")).hasRole("PROVIDER")
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/events/event-creation")).hasRole("ORGANIZER")
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/cities/all")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("api/v1/account/**")).authenticated()
                        .requestMatchers(new AntPathRequestMatcher("api/v1/account/services")).hasRole("PROVIDER")
                        .requestMatchers(new AntPathRequestMatcher("api/v1/account/products")).hasRole("PROVIDER")
                        .requestMatchers(new AntPathRequestMatcher("api/v1/event-types/all")).authenticated()
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/products/top-five-products")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/services/top-five-services")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/events/top-five-events")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/home")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/services/**")).permitAll()
                // TODO: Add access rules for all API endpoints.
        )
        .sessionManagement(session -> {
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        })
        .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        http.authenticationProvider(authenticationProvider());
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/login")
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/register")

                .requestMatchers(HttpMethod.GET, "/", "/webjars/*", "/*.html", "favicon.ico",
                        "/*/*.html", "/*/*.css", "/*/*.js");

    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
