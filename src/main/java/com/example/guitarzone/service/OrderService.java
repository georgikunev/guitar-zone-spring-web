package com.example.guitarzone.service;

import com.example.guitarzone.model.dtos.OrderDTO;
import com.example.guitarzone.model.entities.Order;

import java.util.List;

public interface OrderService {

    void placeOrder(OrderDTO orderDTO, Long userId);
    List<Order> getOrderHistory(Long userId);

}
