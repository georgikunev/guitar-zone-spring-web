package com.example.guitarzone.controllers;

import com.example.guitarzone.model.entities.Product;
import com.example.guitarzone.service.UserService;
import com.example.guitarzone.service.WishlistService;
import com.example.guitarzone.service.impl.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.Set;

@Controller
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping("/users/wishlist")
    public String showWishlist(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userDetails.getId();
        Set<Product> wishlistItems = wishlistService.getWishlistItems(userId);
        model.addAttribute("wishlistItems", wishlistItems);
        return "user-wishlist";
    }

    @PostMapping("/users/wishlist/add")
    public String addToWishlist(@RequestParam Long productId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        wishlistService.addToWishlist(userDetails.getId(), productId);
        return "redirect:/users/wishlist";
    }

    @PostMapping("/users/wishlist/remove")
    public String removeFromWishlist(@RequestParam Long productId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        wishlistService.removeFromWishlist(userDetails.getId(), productId);
        return "redirect:/users/wishlist";
    }

}