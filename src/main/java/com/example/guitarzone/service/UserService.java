package com.example.guitarzone.service;

import com.example.guitarzone.model.dtos.UserAccountInfoDTO;
import com.example.guitarzone.model.dtos.UserRegistrationDTO;
import com.example.guitarzone.model.entities.Product;
import com.example.guitarzone.model.entities.User;

import java.util.Set;

public interface UserService {

    boolean registerUser(UserRegistrationDTO userRegistrationDTO);
    User findByEmail(String email);
    void updateUserAccount(String email, UserAccountInfoDTO userAccountInfoDTO);
    UserAccountInfoDTO getAccountDetails(String email);
    Set<Product> getWishlistItems(Long userId);
    void addToWishlist(Long userId, Long productId);

    void removeFromWishlist(Long userId, Long productId);

}
