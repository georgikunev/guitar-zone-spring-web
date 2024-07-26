package com.example.guitarzone.controllers;

import com.example.guitarzone.model.dtos.OrderAdminDTO;
import com.example.guitarzone.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String showManageOrdersPage(Model model) {
        List<OrderAdminDTO> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "manage-orders";
    }

    @GetMapping("/details/{id}")
    public String showOrderDetails(@PathVariable Long id, Model model) {
        OrderAdminDTO order = orderService.getOrderById(id);
        model.addAttribute("order", order);
        return "order-details";
    }

    @PostMapping("/update-status")
    public String updateOrderStatus(@RequestParam Long orderId, @RequestParam String status) {
        orderService.updateOrderStatus(orderId, status);
        return "redirect:/admin/orders";
    }

    @PostMapping("/delete")
    public String deleteOrder(@RequestParam Long orderId) {
        orderService.deleteOrder(orderId);
        return "redirect:/admin/orders";
    }
}
