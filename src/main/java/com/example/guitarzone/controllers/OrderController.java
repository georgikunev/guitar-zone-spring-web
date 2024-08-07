package com.example.guitarzone.controllers;

import com.example.guitarzone.model.dtos.CartDTO;
import com.example.guitarzone.model.dtos.OrderDTO;
import com.example.guitarzone.model.entities.Order;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

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
    public String placeOrder(@Valid @ModelAttribute("orderDTO") OrderDTO orderDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes, @AuthenticationPrincipal UserDetails userDetails, Model model) {

        if (bindingResult.hasErrors()) {
            Long userId = userService.findByEmail(userDetails.getUsername()).getId();
            CartDTO cart = cartService.getCartByUserId(userId);
            model.addAttribute("cart", cart);
            model.addAttribute("isCartEmpty", cart.getItems().isEmpty());
            return "checkout";
        }


        Long userId = userService.findByEmail(userDetails.getUsername()).getId();
        if (cartService.isCartEmpty(userId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your cart is empty. Please add items to your cart before proceeding to checkout.");
            return "redirect:/cart";
        }
        orderService.placeOrder(orderDTO, userId);

        model.addAttribute("orderConfirmation", "Your order has been placed successfully!");
        return "order-confirmation";
    }

    @GetMapping("/checkout")
    public String showCheckoutPage(@AuthenticationPrincipal UserDetails userDetails, Model model, RedirectAttributes redirectAttributes) {
        Long userId = userService.findByEmail(userDetails.getUsername()).getId();
        CartDTO cart = cartService.getCartByUserId(userId);
        model.addAttribute("cart", cart);
        model.addAttribute("isCartEmpty", cart.getItems().isEmpty());
        model.addAttribute("orderDTO", new OrderDTO());
        if (cartService.isCartEmpty(userId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your cart is empty. Please add items to your cart before proceeding to checkout.");
            return "redirect:/cart";
        }
        return "checkout";
    }

    @GetMapping("/users/orders")
    public String getOrderHistory(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Long userId = userService.findByEmail(userDetails.getUsername()).getId();
        List<Order> orders = orderService.getOrderHistory(userId);
        model.addAttribute("orders", orders);
        return "order-history";
    }
}
