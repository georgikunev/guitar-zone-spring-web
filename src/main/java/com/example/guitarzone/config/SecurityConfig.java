package com.example.guitarzone.config;

import com.example.guitarzone.repositories.UserRepository;
import com.example.guitarzone.service.impl.AppUserDetailsServiceImpl;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.authorizeHttpRequests(
                        authorizeRequests ->
                                authorizeRequests
                                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                                        .requestMatchers("/fonts/**").permitAll()
                                        .requestMatchers("/", "/about", "/contact", "/guitars", "/guitars/guitar-details/**", "/users/login", "users/register").permitAll()
                                        .anyRequest()
                                        .authenticated()
                )
                .formLogin(
                        formLogin ->
                                formLogin.loginPage("/users/login")
                                        .usernameParameter("email")
                                        .passwordParameter("password")
                                        .defaultSuccessUrl("/", true)
                                        .failureUrl("/users/login?error=true")
                )
                .logout(
                        logout ->
                                logout.logoutUrl("/users/logout")
                                        .logoutSuccessUrl("/")
                                        .invalidateHttpSession(true)
                )
                .build();
    }

    @Bean
    AppUserDetailsServiceImpl userDetailsService(UserRepository userRepository) {
        return new AppUserDetailsServiceImpl(userRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }
}
