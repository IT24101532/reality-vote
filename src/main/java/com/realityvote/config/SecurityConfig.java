package com.realityvote.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // ===== Chain 0: EVERYTHING under /viewer/** is open (GET/POST), CSRF off =====
    @Bean
    @Order(0)
    public SecurityFilterChain viewerChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/viewer/**")     // Only applies to /viewer/**
                .csrf(csrf -> csrf.disable())      // allow anonymous POST
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );
        return http.build();
    }

    // ===== Chain 1: rest of the app (admin/contestant + login) =====
    @Bean
    @Order(1)
    public SecurityFilterChain appChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(h -> h.frameOptions(f -> f.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login",
                                "/css/**", "/images/**", "/js/**", "/webjars/**",
                                "/uploads/**", "/favicon.ico").permitAll()

                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/contestant/**").hasRole("CONTESTANT")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login").permitAll()
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/default", true)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                );
        return http.build();
    }

    // ===== Users (in-memory demo) =====
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        var admin = User.withUsername("admin@rv.com")
                .password(encoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        var contestant = User.withUsername("alice@rv.com")
                .password(encoder.encode("pass123"))
                .roles("CONTESTANT")
                .build();

        return new InMemoryUserDetailsManager(admin, contestant);
    }
}
