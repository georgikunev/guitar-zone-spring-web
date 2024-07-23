package com.example.guitarzone.controllers;

import com.example.guitarzone.model.dtos.CartDTO;
import com.example.guitarzone.model.dtos.OrderDTO;
import com.example.guitarzone.service.CartService;
import com.example.guitarzone.service.OrderService;
import com.example.guitarzone.service.UserService;
import com.example.guitarzone.service.impl.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;
    private final CartService cartService;

    public OrderController(OrderService orderService, UserService userService, CartService cartService) {
        this.orderService = orderService;
        this.userService = userService;
        this.cartService = cartService;
    }

    @GetMapping("/checkout")
    public String showCheckoutPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        CartDTO cart = cartService.getCartByUserId(userDetails.getId());
        model.addAttribute("cart", cart);
        model.addAttribute("isCartEmpty", cart.getItems().isEmpty());
        return "checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(OrderDTO orderDTO, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findByEmail(userDetails.getUsername()).getId();
        orderService.placeOrder(orderDTO, userId);
        return "redirect:/order-confirmation";
    }

    @GetMapping("/users/orders")
    public String getOrderHistory(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Long userId = userService.findByEmail(userDetails.getUsername()).getId();
        model.addAttribute("orders", orderService.getOrderHistory(userId));
        return "order-history";
    }
}
