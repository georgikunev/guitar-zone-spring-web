package com.example.guitarzone.service;

import com.example.guitarzone.model.entities.Product;

import java.util.List;


public interface WishlistService {
    List<Product> getWishlistItems(Long userId);
    boolean addToWishlist(Long userId, Long productId);
    void removeFromWishlist(Long userId, Long productId);
    void clearWishlist(Long userId);
}
