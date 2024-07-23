package com.example.guitarzone.service.impl;

import com.example.guitarzone.model.dtos.UserAccountInfoDTO;
import com.example.guitarzone.model.dtos.UserRegistrationDTO;
import com.example.guitarzone.model.entities.Product;
import com.example.guitarzone.model.entities.User;
import com.example.guitarzone.repositories.ProductRepository;
import com.example.guitarzone.repositories.UserRepository;
import com.example.guitarzone.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final ProductRepository productRepository;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.productRepository = productRepository;
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

    @Override
    public Set<Product> getWishlistItems(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getWishlist().stream()
                .sorted(Comparator.comparing(Product::getName)) // Sort by product name
                .collect(Collectors.toCollection(LinkedHashSet::new)); // Maintain order
    }

    @Override
    public void addToWishlist(Long userId, Long productId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));
        user.getWishlist().add(product);
        userRepository.save(user);
    }

    @Override
    public void removeFromWishlist(Long userId, Long productId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        user.getWishlist().remove(product);
        userRepository.save(user);
    }

    private User mapToUser(UserRegistrationDTO userRegistrationDTO) {
        User mappedUser = modelMapper.map(userRegistrationDTO, User.class);
        mappedUser.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
        return mappedUser;
    }
}
