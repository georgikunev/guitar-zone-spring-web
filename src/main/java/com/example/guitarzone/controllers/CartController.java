package com.example.guitarzone.controllers;

import com.example.guitarzone.model.dtos.CartDTO;
import com.example.guitarzone.model.dtos.OrderDTO;
import com.example.guitarzone.model.entities.Cart;
import com.example.guitarzone.service.CartService;
import com.example.guitarzone.service.UserService;
import com.example.guitarzone.service.impl.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping
    public String getCart(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userService.findByEmail(userDetails.getUsername()).getId();
        CartDTO cart = cartService.getCartByUserId(userId);
        model.addAttribute("cart", cart);
        return "cart";
    }

    @PostMapping("/add")
    public String addItemToCart(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @RequestParam Long productId,
                                @RequestParam int quantity) {
        Long userId = userService.findByEmail(userDetails.getUsername()).getId();
        cartService.addItemToCart(userId, productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeItemFromCart(@AuthenticationPrincipal CustomUserDetails userDetails,
                                     @RequestParam Long itemId) {
        Long userId = userService.findByEmail(userDetails.getUsername()).getId();
        cartService.removeItemFromCart(userId, itemId);
        return "redirect:/cart";
    }

    @PostMapping("/increment")
    public String incrementCartItem(@AuthenticationPrincipal CustomUserDetails userDetails,
                                    @RequestParam Long itemId) {
        Long userId = userService.findByEmail(userDetails.getUsername()).getId();
        cartService.incrementCartItem(userId, itemId);
        return "redirect:/cart";
    }

    @PostMapping("/decrement")
    public String decrementCartItem(@AuthenticationPrincipal CustomUserDetails userDetails,
                                    @RequestParam Long itemId) {
        Long userId = userService.findByEmail(userDetails.getUsername()).getId();
        cartService.decrementCartItem(userId, itemId);
        return "redirect:/cart";
    }

    @PostMapping("/proceed-to-checkout")
    public String proceedToCheckout(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userService.findByEmail(userDetails.getUsername()).getId();
        CartDTO cart = cartService.getCartByUserId(userId);
        model.addAttribute("cart", cart);
        model.addAttribute("isCartEmpty", cart.getItems().isEmpty());
        model.addAttribute("orderDTO", new OrderDTO());
        return "checkout";
    }
}

