package com.example.guitarzone.controllers;

import com.example.guitarzone.model.entities.Product;
import com.example.guitarzone.service.UserService;
import com.example.guitarzone.service.impl.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

@Controller
public class WishlistController {

    private final UserService userService;

    public WishlistController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/wishlist")
    public String showWishlist(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userDetails.getId();  // Assuming CustomUserDetails has a getId() method.
        Set<Product> wishlistItems = userService.getWishlistItems(userId);
        model.addAttribute("wishlistItems", wishlistItems);
        return "user-wishlist";
    }

    @PostMapping("/users/wishlist/add")
    public String addToWishlist(@RequestParam Long productId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.addToWishlist(userDetails.getId(), productId);
        return "redirect:/users/wishlist";
    }

    @PostMapping("/users/wishlist/remove")
    public String removeFromWishlist(@RequestParam Long productId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.removeFromWishlist(userDetails.getId(), productId);
        return "redirect:/users/wishlist";
    }
}
