package com.example.guitarzone.controllers;

import com.example.guitarzone.model.dtos.CartDTO;
import com.example.guitarzone.model.dtos.OrderDTO;
import com.example.guitarzone.service.CartService;
import com.example.guitarzone.service.OrderService;
import com.example.guitarzone.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    @PostMapping("/checkout")
    public String placeOrder(@Valid @ModelAttribute("orderDTO") OrderDTO orderDTO, BindingResult bindingResult, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (bindingResult.hasErrors()) {
            Long userId = userService.findByEmail(userDetails.getUsername()).getId();
            CartDTO cart = cartService.getCartByUserId(userId);
            model.addAttribute("cart", cart);
            model.addAttribute("isCartEmpty", cart.getItems().isEmpty());
            return "checkout";
        }

        Long userId = userService.findByEmail(userDetails.getUsername()).getId();
        orderService.placeOrder(orderDTO, userId);

        model.addAttribute("orderConfirmation", "Your order has been placed successfully!");
        return "order-confirmation";
    }

    @GetMapping("/checkout")
    public String showCheckoutPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Long userId = userService.findByEmail(userDetails.getUsername()).getId();
        CartDTO cart = cartService.getCartByUserId(userId);
        model.addAttribute("cart", cart);
        model.addAttribute("isCartEmpty", cart.getItems().isEmpty());
        model.addAttribute("orderDTO", new OrderDTO());
        return "checkout";
    }

    @GetMapping("/users/orders")
    public String getOrderHistory(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Long userId = userService.findByEmail(userDetails.getUsername()).getId();
        model.addAttribute("orders", orderService.getOrderHistory(userId));
        return "order-history";
    }
}
