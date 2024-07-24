package com.example.guitarzone.service;

import com.example.guitarzone.model.dtos.CartDTO;
import com.example.guitarzone.model.dtos.OrderDTO;

import java.util.List;

public interface OrderService {

    void placeOrder(OrderDTO orderDTO, Long userId);
    List<OrderDTO> getOrderHistory(Long userId);

}
