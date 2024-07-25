package com.example.guitarzone.service;

import com.example.guitarzone.model.entities.Product;

import java.util.Set;

public interface WishlistService {
    Set<Product> getWishlistItems(Long userId);
    void addToWishlist(Long userId, Long productId);
    void removeFromWishlist(Long userId, Long productId);
    void clearWishlist(Long userId);
}
