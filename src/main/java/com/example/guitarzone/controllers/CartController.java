package com.example.guitarzone.controllers;

import com.example.guitarzone.model.dtos.CartDTO;
import com.example.guitarzone.model.entities.Cart;
import com.example.guitarzone.service.CartService;
import com.example.guitarzone.service.impl.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/cart")
    public String viewCart(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        CartDTO cartDTO = cartService.getCartByUserId(userDetails.getId());
        model.addAttribute("cart", cartDTO);
        return "cart";
    }

    @PostMapping("/cart/add")
    public String addItemToCart(@RequestParam Long productId, @RequestParam int quantity, @AuthenticationPrincipal CustomUserDetails userDetails) {
        cartService.addItemToCart(userDetails.getId(), productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/cart/update")
    public String updateItemQuantity(@RequestParam Long itemId, @RequestParam int quantity, @AuthenticationPrincipal CustomUserDetails userDetails) {
        cartService.updateCartItemQuantity(userDetails.getId(), itemId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove")
    public String removeItemFromCart(@RequestParam Long itemId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        cartService.removeItemFromCart(userDetails.getId(), itemId);
        return "redirect:/cart";
    }
}
