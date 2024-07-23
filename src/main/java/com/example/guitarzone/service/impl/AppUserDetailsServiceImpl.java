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

import java.util.List;
import java.util.stream.Collectors;


public class AppUserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public AppUserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return toUserDetails(user);
    }

    private UserDetails toUserDetails(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(Role::getRole)  // Assuming Role has a getName method to return the name of the role.
                .map(roleName -> new SimpleGrantedAuthority("ROLE_" + roleName))
                .collect(Collectors.toList());

        return new CustomUserDetails(user, authorities);
    }

    private static GrantedAuthority toGrantedAuthority(UserRole role) {
        return new SimpleGrantedAuthority(
                "ROLE_" + role
        );
    }
}
