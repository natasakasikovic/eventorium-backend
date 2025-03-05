package com.iss.eventorium.config;

import com.iss.eventorium.security.auth.JwtRequestFilter;
import com.iss.eventorium.security.auth.RestAuthenticationEntryPoint;
import com.iss.eventorium.user.services.CustomUserDetailsService;
import com.iss.eventorium.user.services.UserService;
import com.iss.eventorium.security.utils.JwtTokenUtil;
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
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final JwtTokenUtil jwtTokenUtil;

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
                        .requestMatchers("api/v1/ws/**").authenticated()

                        // Services
                        .requestMatchers("api/v1/services/top-five-services").permitAll()
                        .requestMatchers(HttpMethod.GET, "api/v1/services/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "api/v1/services").permitAll()
                        .requestMatchers("api/v1/services/all").permitAll()
                        .requestMatchers("api/v1/services/filter").permitAll()
                        .requestMatchers("api/v1/services/search").permitAll()
                        .requestMatchers("api/v1/services/{id}/*").permitAll()

                        // Products
                        .requestMatchers("api/v1/products/top-five-services").permitAll()
                        .requestMatchers(HttpMethod.GET, "api/v1/products/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "api/v1/products").permitAll()
                        .requestMatchers("api/v1/products/all").permitAll()
                        .requestMatchers("api/v1/products/filter").permitAll()
                        .requestMatchers("api/v1/products/search").permitAll()
                        .requestMatchers("api/v1/products/{id}/*").permitAll()

                        // Events
                        .requestMatchers("api/v1/events/top-five-services").permitAll()
                        .requestMatchers(HttpMethod.GET, "api/v1/events/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "api/v1/events").permitAll()
                        .requestMatchers("api/v1/events/all").permitAll()
                        .requestMatchers("api/v1/events/filter").permitAll()
                        .requestMatchers("api/v1/events/search").permitAll()
                        .requestMatchers("api/v1/events/{id}/*").permitAll()

                        // Users
                        .requestMatchers(HttpMethod.GET, "api/v1/users/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "api/v1/users/{id}/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "api/v1/users/me").authenticated()

                        // Companies
                        .requestMatchers(HttpMethod.GET, "api/v1/companies/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "api/v1/companies/{id}/*").permitAll()
                        .requestMatchers("/api/v1/companies/{id}/images").permitAll()

                        // Event types
                        .requestMatchers("api/v1/event-types/all").permitAll()
                        // Categories
                        .requestMatchers("api/v1/categories/all").permitAll()

                        // Others
                        .requestMatchers("api/v1/roles/registration-options").permitAll()
                        .requestMatchers("api/v1/cities/all").permitAll()
                        .requestMatchers("api/v1/auth/activation/{hash}").permitAll()
                        .requestMatchers("api/v1/auth/{id}/profile-photo").permitAll()

//                        .requestMatchers("**").permitAll()
                // TODO: Add access rules for all API endpoints.
        )
        .sessionManagement(session -> {
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        })
        .addFilterBefore(new JwtRequestFilter(jwtTokenUtil, userDetailsService()), UsernamePasswordAuthenticationFilter.class);
        http.authenticationProvider(authenticationProvider());
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/login")
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/registration")
                .requestMatchers(HttpMethod.POST, "/api/v1/companies")

                .requestMatchers(HttpMethod.GET, "/", "/webjars/*", "/*.html", "favicon.ico",
                        "/*/*.html", "/*/*.css", "/*/*.js");

    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
