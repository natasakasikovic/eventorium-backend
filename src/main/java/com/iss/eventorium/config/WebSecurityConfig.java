package com.iss.eventorium.config;

import com.iss.eventorium.security.auth.JwtRequestFilter;
import com.iss.eventorium.security.auth.RestAuthenticationEntryPoint;
import com.iss.eventorium.user.services.CustomUserDetailsService;
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
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService customUserDetailsService;

    private static final String PROVIDER = "PROVIDER";
    private static final String ORGANIZER = "EVENT_ORGANIZER";
    private static final String ADMIN = "ADMIN";

    @Bean
    public UserDetailsService userDetailsService() {
        return customUserDetailsService;
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
        http.csrf(AbstractHttpConfigurer::disable);
        http.exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(restAuthenticationEntryPoint));
        http.authorizeHttpRequests(request -> request
                        .requestMatchers("/api/v1/ws/**").permitAll()
                        .requestMatchers("/api/v1/ws").permitAll()

                        // Services
                        .requestMatchers("/api/v1/services/top-five-services").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/services/{id}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/services").hasAuthority(PROVIDER)
                        .requestMatchers(HttpMethod.PUT, "/api/v1/services/{id}").hasAuthority(PROVIDER)
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/services/{id}").hasAuthority(PROVIDER)
                        .requestMatchers(HttpMethod.GET, "/api/v1/services").permitAll()
                        .requestMatchers("/api/v1/services/all").permitAll()
                        .requestMatchers("/api/v1/services/filter").permitAll()
                        .requestMatchers("/api/v1/services/search").permitAll()
                        .requestMatchers("/api/v1/services/filter/all").permitAll()
                        .requestMatchers("/api/v1/services/search/all").permitAll()
                        .requestMatchers("/api/v1/services/suggestions").hasAuthority(ORGANIZER)
                        .requestMatchers(HttpMethod.POST, "/api/v1/services/{id}/images").hasAuthority(PROVIDER)
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/services/{id}/images").hasAuthority(PROVIDER)
                        .requestMatchers("/api/v1/services/{id}/*").permitAll()

                        // Products
                        .requestMatchers("/api/v1/products/top-five-services").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/products").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/products").hasAuthority(PROVIDER)
                        .requestMatchers(HttpMethod.PUT, "/api/v1/products/{id}").hasAuthority(PROVIDER)
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/products/{id}").hasAuthority(PROVIDER)
                        .requestMatchers("/api/v1/products/all").permitAll()
                        .requestMatchers("/api/v1/products/filter").permitAll()
                        .requestMatchers("/api/v1/products/search").permitAll()
                        .requestMatchers("/api/v1/products/filter/all").permitAll()
                        .requestMatchers("/api/v1/products/search/all").permitAll()
                        .requestMatchers("/api/v1/products/suggestions").hasAuthority(ORGANIZER)
                        .requestMatchers("/api/v1/products/{id}/*").permitAll()

                        // Events
                        .requestMatchers(HttpMethod.PUT, "/api/v1/events/{id}").hasAuthority(ORGANIZER)
                        .requestMatchers(HttpMethod.POST, "/api/v1/events").hasAuthority(ORGANIZER)
                        .requestMatchers(HttpMethod.POST, "/api/v1/events/**").hasAuthority(ORGANIZER)
                        .requestMatchers(HttpMethod.PUT, "/api/v1/events/*/agenda").hasAuthority(ORGANIZER)
                        .requestMatchers("/api/v1/events/drafted").hasAuthority(ORGANIZER)
                        .requestMatchers("/api/v1/events/{id}/guest-list-pdf").hasAuthority(ORGANIZER)
                        .requestMatchers("/api/v1/events/top-five-services").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/events/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/events").permitAll()
                        .requestMatchers("/api/v1/events/all").permitAll()
                        .requestMatchers("/api/v1/events/filter").permitAll()
                        .requestMatchers("/api/v1/events/search").permitAll()
                        .requestMatchers("/api/v1/events/filter/all").permitAll()
                        .requestMatchers("/api/v1/events/search/all").permitAll()
                        .requestMatchers("/api/v1/events/{id}/*").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/events/{id}").hasAuthority(ORGANIZER)
                        .requestMatchers(HttpMethod.POST, "/api/v1/events").hasAuthority(ORGANIZER)
                        .requestMatchers(HttpMethod.POST, "/api/v1/events/**").hasAuthority(ORGANIZER)

                        // Users
                        .requestMatchers(HttpMethod.PUT, "/api/v1/users").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/users").authenticated()
                        .requestMatchers("/api/v1/users/password").authenticated()
                        .requestMatchers("/api/v1/users/profile-photo").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/{id}/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/me").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/user-reports/{id}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/user-reports").hasAuthority(ADMIN)
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/user-reports/{id}").hasAuthority(ADMIN)
                        .requestMatchers("/api/v1/user-blocking/*").authenticated()

                        // Companies
                        .requestMatchers("/api/v1/companies/my-company").hasAuthority(PROVIDER)
                        .requestMatchers(HttpMethod.PUT, "/api/v1/companies").hasAuthority(PROVIDER)
                        .requestMatchers(HttpMethod.PUT, "/api/v1/companies/images").hasAuthority(PROVIDER)
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/companies/images").hasAuthority(PROVIDER)
                        .requestMatchers(HttpMethod.GET, "/api/v1/companies/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/companies/{id}/*").permitAll()
                        .requestMatchers("/api/v1/companies/{id}/images").permitAll()

                        // Event types
                        .requestMatchers("/api/v1/event-types/all").permitAll()
                        .requestMatchers("/api/v1/event-types").hasAuthority(ADMIN)
                        .requestMatchers("/api/v1/event-types/*").hasAuthority(ADMIN)

                        // Categories
                        .requestMatchers("/api/v1/categories/all").permitAll()
                        .requestMatchers("/api/v1/categories").hasAuthority(ADMIN)
                        .requestMatchers("/api/v1/categories/**").hasAuthority(ADMIN)

                        // Notifications
                        .requestMatchers("/api/v1/notifications").authenticated()
                        .requestMatchers("/api/v1/notifications/seen").authenticated()

                        // Account Events
                        .requestMatchers("/api/v1/account/events").hasAuthority(ORGANIZER)
                        .requestMatchers("/api/v1/account/events/all").hasAuthority(ORGANIZER)
                        .requestMatchers("/api/v1/account/events/search/all").hasAuthority(ORGANIZER)
                        .requestMatchers("/api/v1/account/events/search").hasAuthority(ORGANIZER)
                        .requestMatchers("/api/v1/account/events/{id}/attendance").authenticated()
                        .requestMatchers("/api/v1/account/events/favourites").authenticated()
                        .requestMatchers("/api/v1/account/events/favourites/{id}").authenticated()
                        .requestMatchers("/api/v1/account/events/my-attending-events").authenticated()

                        // Account Services
                        .requestMatchers("/api/v1/account/services").hasAuthority(PROVIDER)
                        .requestMatchers("/api/v1/account/services/all").hasAuthority(PROVIDER)
                        .requestMatchers("/api/v1/account/services/search/all").hasAuthority(PROVIDER)
                        .requestMatchers("/api/v1/account/services/search").hasAuthority(PROVIDER)
                        .requestMatchers("/api/v1/account/services/filter/all").hasAuthority(PROVIDER)
                        .requestMatchers("/api/v1/account/services/filter").hasAuthority(PROVIDER)
                        .requestMatchers("/api/v1/account/services/favourites").authenticated()
                        .requestMatchers("/api/v1/account/services/favourites/{id}").authenticated()

                        // Account Products
                        .requestMatchers("/api/v1/account/products").hasAuthority(PROVIDER)
                        .requestMatchers("/api/v1/account/products/all").hasAuthority(PROVIDER)
                        .requestMatchers("/api/v1/account/products/search/all").hasAuthority(PROVIDER)
                        .requestMatchers("/api/v1/account/products/search").hasAuthority(PROVIDER)
                        .requestMatchers("/api/v1/account/products/filter/all").hasAuthority(PROVIDER)
                        .requestMatchers("/api/v1/account/products/filter").hasAuthority(PROVIDER)
                        .requestMatchers("/api/v1/account/products/favourites").authenticated()
                        .requestMatchers("/api/v1/account/products/favourites/{id}").authenticated()

                        // Interactions
                        .requestMatchers("/api/v1/comments/**").hasAuthority(ADMIN)
                        .requestMatchers("/api/v1/messages/**").authenticated()
                        .requestMatchers("/api/v1/chat-rooms").authenticated()
                        .requestMatchers("/api/v1/chat-rooms/all").authenticated()

                        // Others
                        .requestMatchers("/api/v1/roles/registration-options").permitAll()
                        .requestMatchers("/api/v1/auth/quick-registration").permitAll()
                        .requestMatchers("/api/v1/cities/all").permitAll()
                        .requestMatchers("/api/v1/auth/activation/{hash}").permitAll()
                        .requestMatchers("/api/v1/auth/{id}/profile-photo").permitAll()
                        .requestMatchers("/api/v1/budget-items").hasAuthority(ORGANIZER)
                        .requestMatchers("/api/v1/provider-reservations").hasAuthority(PROVIDER)
                        .requestMatchers("/api/v1/reservations/pending").hasAuthority(PROVIDER)
                        .requestMatchers("/api/v1/price-list/**").hasAuthority(PROVIDER)
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/reservations/{id}").hasAuthority(PROVIDER)
                        .requestMatchers("/api/v1/price-list/**").hasAuthority(PROVIDER)
                        // Invitations
                        .requestMatchers("/api/v1/invitations/verification/{hash}").permitAll()
                        .requestMatchers("/api/v1/invitations/my-invitations").authenticated()
                        .requestMatchers("/api/v1/invitations/*").hasAuthority(ORGANIZER)

        )
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(new JwtRequestFilter(jwtTokenUtil, userDetailsService()), UsernamePasswordAuthenticationFilter.class);
        http.authenticationProvider(authenticationProvider());
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
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
