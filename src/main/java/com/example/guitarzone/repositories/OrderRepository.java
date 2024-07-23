package com.example.guitarzone.repositories;

import com.example.guitarzone.model.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface OrderRepository extends JpaRepository<Order, Long> {


    Collection<Object> findByUserId(Long userId);
}
