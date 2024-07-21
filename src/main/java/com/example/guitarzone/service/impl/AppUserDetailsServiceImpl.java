package com.example.guitarzone.service.impl;

import com.example.guitarzone.model.entities.Role;
import com.example.guitarzone.model.entities.User;
import com.example.guitarzone.model.enums.UserRole;
import com.example.guitarzone.repositories.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


public class AppUserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public AppUserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        return  userRepository
                .findByEmail(email)
                .map(AppUserDetailsServiceImpl::map)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " not found"));
    }

    private static UserDetails map(User user) {
        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRoles().stream().map(Role::getRole).map(AppUserDetailsServiceImpl::map).toList())
                .disabled(false)
                .build();
    }
    private static GrantedAuthority map(UserRole role) {
        return new SimpleGrantedAuthority(
                "ROLE_" + role
        );
    }
}
