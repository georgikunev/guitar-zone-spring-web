package com.example.guitarzone.service.impl;

import com.example.guitarzone.model.dtos.CartDTO;
import com.example.guitarzone.model.dtos.OrderAdminDTO;
import com.example.guitarzone.model.dtos.OrderDTO;
import com.example.guitarzone.model.dtos.OrderItemDTO;
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

import java.time.Instant;
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
        order.setOrderDate(Instant.now());
        order.setAddress(orderDTO.getAddress());
        order.setCountry(orderDTO.getCountry());
        order.setCity(orderDTO.getCity());
        order.setZip(orderDTO.getZip());
        order.setTotalAmount(cartDTO.getTotal());
        order.setStatus("Pending");

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
            orderItem.setProductName(product.getName());
            orderItem.setProductPrice(product.getPrice());
            orderItem.setProductQuantity(product.getQuantity());
            return orderItem;
        }).collect(Collectors.toList());
        order.setOrderItems(orderItems);
        orderRepository.save(order);
        cartService.clearCart(userId);
    }

    @Override
    public List<Order> getOrderHistory(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public List<OrderAdminDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(this::mapToAdminDTO).collect(Collectors.toList());
    }

    @Override
    public OrderAdminDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        return mapToAdminDTO(order);
    }

    @Override
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    private OrderAdminDTO mapToAdminDTO(Order order) {
        OrderAdminDTO orderAdminDTO = new OrderAdminDTO();
        orderAdminDTO.setId(order.getId());
        orderAdminDTO.setOrderDate(order.getOrderDate());
        orderAdminDTO.setAddress(order.getAddress());
        orderAdminDTO.setCountry(order.getCountry());
        orderAdminDTO.setCity(order.getCity());
        orderAdminDTO.setZip(order.getZip());
        orderAdminDTO.setTotalAmount(order.getTotalAmount());
        orderAdminDTO.setStatus(order.getStatus());
        orderAdminDTO.setCustomerFullName(order.getUser().getFirstName() + " " + order.getUser().getLastName());
        orderAdminDTO.setOrderItems(order.getOrderItems().stream()
                .map(this::mapToOrderItemDTO)
                .collect(Collectors.toList()));
        return orderAdminDTO;
    }

    private OrderItemDTO mapToOrderItemDTO(OrderItem orderItem) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(orderItem.getProduct() != null ? orderItem.getProduct().getId() : null);
        dto.setProductName(orderItem.getProductName());
        dto.setPrice(orderItem.getProductPrice());
        dto.setQuantity(orderItem.getQuantity());
        return dto;
    }
}
