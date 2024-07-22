package com.example.guitarzone.service.impl;

import com.example.guitarzone.model.dtos.UserAccountInfoDTO;
import com.example.guitarzone.model.dtos.UserRegistrationDTO;
import com.example.guitarzone.model.entities.User;
import com.example.guitarzone.repositories.UserRepository;
import com.example.guitarzone.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public boolean registerUser(UserRegistrationDTO userRegistrationDTO) {
        Optional<User> existingUser = userRepository
                .findByEmail(userRegistrationDTO.getEmail());

        if (existingUser.isPresent()) {
            return false;
        }

        this.userRepository.save(map(userRegistrationDTO));

        return true;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Override
    public void updateUserAccount(String email, UserAccountInfoDTO userAccountInfoDTO) {
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        // Map DTO to existing user entity
        modelMapper.map(userAccountInfoDTO, existingUser);

        userRepository.save(existingUser);
    }

    @Override
    public UserAccountInfoDTO getAccountDetails(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No user found with email " + email));
        UserAccountInfoDTO dto = new UserAccountInfoDTO();
        modelMapper.map(user, dto);
        return dto;
    }

    private User map(UserRegistrationDTO userRegistrationDTO) {
        User mappedUser = modelMapper.map(userRegistrationDTO, User.class);
        mappedUser.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
        return mappedUser;
    }
}
