package com.example.guitarzone.service.impl;

import com.example.guitarzone.model.entities.Product;
import com.example.guitarzone.model.entities.User;
import com.example.guitarzone.repositories.ProductRepository;
import com.example.guitarzone.repositories.UserRepository;
import com.example.guitarzone.service.WishlistService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WishlistServiceImpl implements WishlistService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public WishlistServiceImpl(UserRepository userRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getWishlistItems(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getWishlist().stream()
                .sorted(Comparator.comparing(Product::getName))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public boolean addToWishlist(Long userId, Long productId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (user.getWishlist().contains(product)) {
            return false;
        } else {
            user.getWishlist().add(product);
            userRepository.save(user);
            return true;
        }
    }

    @Override
    public void removeFromWishlist(Long userId, Long productId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        user.getWishlist().remove(product);
        userRepository.save(user);
    }

    @Override
    public void clearWishlist(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.getWishlist().clear();
        userRepository.save(user);
    }

}
