package com.example.guitarzone.service.impl;

import com.example.guitarzone.model.dtos.UserAccountInfoDTO;
import com.example.guitarzone.model.dtos.UserRegistrationDTO;
import com.example.guitarzone.model.entities.User;
import com.example.guitarzone.repositories.UserRepository;
import com.example.guitarzone.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

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

        this.userRepository.save(mapToUser(userRegistrationDTO));

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

        modelMapper.map(userAccountInfoDTO, existingUser);

        userRepository.save(existingUser);
    }

    @Override
    public void updateUserName(String email, String firstName, String lastName) {
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        existingUser.setFirstName(firstName);
        existingUser.setLastName(lastName);
        userRepository.save(existingUser);
    }

    @Override
    public void updateUserPhone(String email, String phoneNumber) {
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        existingUser.setPhoneNumber(phoneNumber);
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

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    private User mapToUser(UserRegistrationDTO userRegistrationDTO) {
        User mappedUser = modelMapper.map(userRegistrationDTO, User.class);
        mappedUser.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
        return mappedUser;
    }
}
