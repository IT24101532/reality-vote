package com.realityvote.config;

import com.realityvote.model.enums.Role;
import com.realityvote.repository.UserAccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.HiddenHttpMethodFilter;

@Configuration
public class SecurityConfig {

    // ================== 🔹 Viewer Filter Chain (handles /viewer/** separately) ==================
    @Bean
    @Order(1)
    public SecurityFilterChain viewerFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/viewer/**") // applies only to viewer routes
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/viewer/register", "/viewer/register/save", "/viewer/otp/**", "/viewer/verify", "/viewer/validate-otp").permitAll()
                        .anyRequest().hasRole(Role.VIEWER.name())
                )
                .formLogin(form -> form
                        .loginPage("/viewer/login")
                        .loginProcessingUrl("/viewer/login")
                        .defaultSuccessUrl("/viewer/select", true)
                        .failureUrl("/viewer/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/viewer/logout")
                        .logoutSuccessUrl("/viewer/login?logout=true")
                        .permitAll()
                );
        return http.build();
    }

    // ================== 🔹 Main Filter Chain (for admin + contestant) ==================
    @Bean
    @Order(2)
    public SecurityFilterChain mainFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/index", "/login",
                                "/css/**", "/js/**", "/images/**",
                                "/whats-new", "/error", "/faq/query/save"
                        ).permitAll()
                        .requestMatchers("/admin/**").hasRole(Role.ADMIN.name())
                        .requestMatchers("/contestant/**").hasAnyRole(Role.CONTESTANT.name(), Role.ADMIN.name())
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/post-login", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );
        return http.build();
    }

    // ================== 🔹 Common Beans ==================
    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserAccountRepository repo) {
        return username -> repo.findByUsername(username)
                .map(u -> org.springframework.security.core.userdetails.User.withUsername(u.getUsername())
                        .password(u.getPasswordHash())
                        .roles(u.getRole().name())
                        .disabled(!u.isEnabled())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public DaoAuthenticationProvider authProvider(UserDetailsService uds, BCryptPasswordEncoder enc) {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(uds);
        p.setPasswordEncoder(enc);
        return p;
    }
}
