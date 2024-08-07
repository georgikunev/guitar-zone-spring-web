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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


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
        List<Product> wishlistItems = wishlistService.getWishlistItems(userId);
        model.addAttribute("wishlistItems", wishlistItems);

        if (wishlistItems.isEmpty()) {
            model.addAttribute("errorMessage", "Your wishlist is currently empty.");
        }

        return "user-wishlist";
    }

    @PostMapping("/users/wishlist/add")
    public String addToWishlist(@RequestParam Long productId, @AuthenticationPrincipal CustomUserDetails userDetails, RedirectAttributes redirectAttributes) {
        boolean added = wishlistService.addToWishlist(userDetails.getId(), productId);
        if (added) {
            redirectAttributes.addFlashAttribute("successMessage", "Product added to wishlist successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Product is already in your wishlist.");
        }
        return "redirect:/guitars";
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
        List<Product> wishlistItems = wishlistService.getWishlistItems(userId);

        for (Product product : wishlistItems) {
            if (product.getQuantity() < 1) {
                model.addAttribute("errorMessage", "Product " + product.getName() + " is out of stock.");
                List<Product> updatedWishlistItems = wishlistService.getWishlistItems(userId);
                model.addAttribute("wishlistItems", updatedWishlistItems);
                return "user-wishlist";
            }

            cartService.addItemToCart(userId, product.getId(), 1);
        }

        wishlistService.clearWishlist(userId);
        return "redirect:/cart";
    }
}