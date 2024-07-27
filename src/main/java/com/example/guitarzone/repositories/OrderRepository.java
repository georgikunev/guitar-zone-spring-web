package com.example.guitarzone.repositories;

import com.example.guitarzone.model.entities.Order;
import com.example.guitarzone.model.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    List<Order> findByOrderItemsProduct(Product product);
}
