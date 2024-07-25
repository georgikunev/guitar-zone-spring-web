package com.example.guitarzone.controllers;

import com.example.guitarzone.model.entities.Product;
import com.example.guitarzone.service.CartService;
import com.example.guitarzone.service.WishlistService;
import com.example.guitarzone.service.impl.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

@Controller
public class WishlistController {

    private final WishlistService wishlistService;
    private final CartService cartService;

    public WishlistController(WishlistService wishlistService, CartService cartService) {
        this.wishlistService = wishlistService;
        this.cartService = cartService;
    }

    @GetMapping("/users/wishlist")
    public String showWishlist(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userDetails.getId();
        Set<Product> wishlistItems = wishlistService.getWishlistItems(userId);
        model.addAttribute("wishlistItems", wishlistItems);

        if (wishlistItems.isEmpty()) {
            model.addAttribute("errorMessage", "Your wishlist is currently empty.");
        }

        return "user-wishlist";
    }

    @PostMapping("/users/wishlist/add")
    public String addToWishlist(@RequestParam Long productId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        wishlistService.addToWishlist(userDetails.getId(), productId);
        return "redirect:/users/wishlist";
    }

    @PostMapping("/users/wishlist/remove")
    public String removeFromWishlist(@RequestParam Long productId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        wishlistService.removeFromWishlist(userId, productId);
        return "redirect:/users/wishlist";
    }

    @PostMapping("/users/wishlist/addAllToCart")
    public String addAllToCart(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userDetails.getId();
        Set<Product> wishlistItems = wishlistService.getWishlistItems(userId);

        for (Product product : wishlistItems) {
            if (product.getQuantity() < 1) {
                model.addAttribute("errorMessage", "Product " + product.getName() + " is out of stock.");
                Set<Product> updatedWishlistItems = wishlistService.getWishlistItems(userId);
                model.addAttribute("wishlistItems", updatedWishlistItems);
                return "user-wishlist";
            }

            cartService.addItemToCart(userId, product.getId(), 1);
        }

        wishlistService.clearWishlist(userId);
        return "redirect:/cart";
    }
}