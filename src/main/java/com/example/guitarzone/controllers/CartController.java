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
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public String viewCart(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        CartDTO cart = cartService.getCartByUserId(userDetails.getId());
        model.addAttribute("cart", cart);
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId, @RequestParam Integer quantity, @AuthenticationPrincipal CustomUserDetails userDetails) {
        cartService.addItemToCart(userDetails.getId(), productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long itemId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        cartService.removeItemFromCart(userDetails.getId(), itemId);
        return "redirect:/cart";
    }

    @PostMapping("/increment")
    public String incrementCartItem(@RequestParam Long itemId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        cartService.incrementCartItem(userDetails.getId(), itemId);
        return "redirect:/cart";
    }

    @PostMapping("/decrement")
    public String decrementCartItem(@RequestParam Long itemId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        cartService.decrementCartItem(userDetails.getId(), itemId);
        return "redirect:/cart";
    }
}