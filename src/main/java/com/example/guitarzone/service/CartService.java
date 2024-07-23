package com.example.guitarzone.service;

import com.example.guitarzone.model.dtos.CartDTO;
import com.example.guitarzone.model.entities.Cart;

public interface CartService {
    CartDTO getCartByUserId(Long userId);

    void addItemToCart(Long userId, Long productId, int quantity);

    void removeItemFromCart(Long userId, Long cartItemId);

    void updateCartItemQuantity(Long userId, Long cartItemId, int quantity);

}
