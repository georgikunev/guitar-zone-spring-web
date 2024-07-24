package com.example.guitarzone.service.impl;

import com.example.guitarzone.model.dtos.CartDTO;
import com.example.guitarzone.model.dtos.OrderDTO;
import com.example.guitarzone.model.entities.Order;
import com.example.guitarzone.model.entities.OrderItem;
import com.example.guitarzone.model.entities.Product;
import com.example.guitarzone.model.entities.User;
import com.example.guitarzone.repositories.OrderRepository;
import com.example.guitarzone.repositories.ProductRepository;
import com.example.guitarzone.repositories.UserRepository;
import com.example.guitarzone.service.CartService;
import com.example.guitarzone.service.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final ModelMapper modelMapper;

    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository, ProductRepository productRepository, CartService cartService, ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
        this.modelMapper = modelMapper;
    }

    @Override
    public void placeOrder(OrderDTO orderDTO, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        CartDTO cartDTO = cartService.getCartByUserId(userId);

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setAddress(orderDTO.getAddress());
        order.setCountry(orderDTO.getCountry());
        order.setCity(orderDTO.getCity());
        order.setZip(orderDTO.getZip());
        order.setTotalAmount(cartDTO.getTotal());

        List<OrderItem> orderItems = cartDTO.getItems().stream().map(cartItemDTO -> {
            Product product = productRepository.findById(cartItemDTO.getProduct().getId()).orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getQuantity() > 0) {
                product.setQuantity(product.getQuantity() - cartItemDTO.getQuantity());
                if (product.getQuantity() == 0) {
                    product.setStockStatus("out of stock");
                }
                productRepository.save(product);
            } else {
                throw new RuntimeException("Product out of stock");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItemDTO.getQuantity());
            orderItem.setPrice(cartItemDTO.getPrice());
            orderItem.setOrder(order);
            return orderItem;
        }).collect(Collectors.toList());

        order.setOrderItems(orderItems);
        orderRepository.save(order);

        cartService.clearCart(userId);
    }

    @Override
    public List<OrderDTO> getOrderHistory(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }
}

