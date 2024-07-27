package com.example.guitarzone.repositories;

import com.example.guitarzone.model.entities.CartItem;
import com.example.guitarzone.model.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByProduct(Product product);
}
