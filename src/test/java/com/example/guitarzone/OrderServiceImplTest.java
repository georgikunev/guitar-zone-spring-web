package com.example.guitarzone;

import com.example.guitarzone.model.dtos.CartDTO;
import com.example.guitarzone.model.dtos.OrderAdminDTO;
import com.example.guitarzone.model.dtos.OrderDTO;
import com.example.guitarzone.model.entities.Order;
import com.example.guitarzone.model.entities.OrderItem;
import com.example.guitarzone.model.entities.Product;
import com.example.guitarzone.model.entities.User;
import com.example.guitarzone.repositories.OrderRepository;
import com.example.guitarzone.repositories.ProductRepository;
import com.example.guitarzone.repositories.UserRepository;
import com.example.guitarzone.service.CartService;
import com.example.guitarzone.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartService cartService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private CartDTO cartDTO;
    private OrderDTO orderDTO;
    private Product product;
    private Order order;


    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setPassword("password");

        orderDTO = new OrderDTO();
        orderDTO.setAddress("123 Test St");
        orderDTO.setCity("Test City");
        orderDTO.setCountry("Test Country");
        orderDTO.setZip("1234");

        cartDTO = new CartDTO();
        cartDTO.setItems(new ArrayList<>());
        cartDTO.setTotal(100.0);

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setQuantity(10);
        product.setPrice(100.0);

        order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setOrderItems(new ArrayList<>());
    }

    @Test
    void testPlaceOrder() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(cartService.getCartByUserId(anyLong())).thenReturn(cartDTO);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        assertDoesNotThrow(() -> orderService.placeOrder(orderDTO, 1L));

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(cartService, times(1)).clearCart(1L);
    }
    @Test
    void testGetOrderHistory() {
        when(orderRepository.findByUserId(anyLong())).thenReturn(List.of(order));

        List<Order> orders = orderService.getOrderHistory(1L);

        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertEquals(order, orders.getFirst());
    }

    @Test
    void testGetAllOrders() {
        when(orderRepository.findAll()).thenReturn(List.of(order));

        List<OrderAdminDTO> orders = orderService.getAllOrders();

        assertNotNull(orders);
        assertEquals(1, orders.size());
    }

    @Test
    void testGetOrderById() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        OrderAdminDTO orderAdminDTO = orderService.getOrderById(1L);

        assertNotNull(orderAdminDTO);
        assertEquals(order.getId(), orderAdminDTO.getId());
    }

    @Test
    void testUpdateOrderStatus() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        orderService.updateOrderStatus(1L, "Shipped");

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testDeleteOrder() {
        doNothing().when(orderRepository).deleteById(anyLong());

        orderService.deleteOrder(1L);

        verify(orderRepository, times(1)).deleteById(1L);
    }
}
