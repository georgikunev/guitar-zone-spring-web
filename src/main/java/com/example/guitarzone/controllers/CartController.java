package com.example.guitarzone.controllers;

import com.example.guitarzone.model.dtos.CartDTO;
import com.example.guitarzone.model.dtos.OrderDTO;
import com.example.guitarzone.service.CartService;
import com.example.guitarzone.service.UserService;
import com.example.guitarzone.service.impl.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
                                @RequestParam int quantity,
                                RedirectAttributes redirectAttributes) {
        Long userId = userService.findByEmail(userDetails.getUsername()).getId();
        String message = cartService.addItemToCart(userId, productId, quantity);

        if ("Product already in cart".equals(message)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Product is already in your cart.");
        } else {
            redirectAttributes.addFlashAttribute("successMessage", "Product added to your cart.");
        }
        return "redirect:/guitars";  // Redirect to guitars page or any other page you prefer
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
                                    @RequestParam Long itemId, Model model) {
        Long userId = userService.findByEmail(userDetails.getUsername()).getId();
        try {
            cartService.incrementCartItem(userId, itemId);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        CartDTO cart = cartService.getCartByUserId(userId);
        model.addAttribute("cart", cart);
        return "cart";
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

