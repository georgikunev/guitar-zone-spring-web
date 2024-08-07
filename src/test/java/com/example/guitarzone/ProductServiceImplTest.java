package com.example.guitarzone;

import com.example.guitarzone.components.ReviewClient;
import com.example.guitarzone.model.dtos.ProductDetailsDTO;
import com.example.guitarzone.model.dtos.ReviewDTO;
import com.example.guitarzone.model.dtos.ShortProductInfoDTO;
import com.example.guitarzone.model.entities.CartItem;
import com.example.guitarzone.model.entities.Order;
import com.example.guitarzone.model.entities.OrderItem;
import com.example.guitarzone.model.entities.Product;
import com.example.guitarzone.repositories.CartItemRepository;
import com.example.guitarzone.repositories.OrderRepository;
import com.example.guitarzone.repositories.ProductRepository;
import com.example.guitarzone.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ReviewClient reviewClient;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductServiceImpl productService;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private OrderRepository orderRepository;

    private Product product;
    private ProductDetailsDTO productDetailsDTO;
    private ShortProductInfoDTO shortProductInfoDTO;
    private List<ReviewDTO> reviews;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Test Guitar");

        productDetailsDTO = new ProductDetailsDTO();
        productDetailsDTO.setId(1L);
        productDetailsDTO.setName("Test Guitar");

        shortProductInfoDTO = new ShortProductInfoDTO();
        shortProductInfoDTO.setId(1L);
        shortProductInfoDTO.setName("Test Guitar");

        reviews = new ArrayList<>();
        ReviewDTO review = new ReviewDTO();
        review.setRating(4);
        reviews.add(review);
    }

    @Test
    public void testGetAllProducts() {
        List<Product> products = new ArrayList<>();
        products.add(product);

        when(productRepository.findAll()).thenReturn(products);
        when(modelMapper.map(product, ShortProductInfoDTO.class)).thenReturn(shortProductInfoDTO);

        List<ShortProductInfoDTO> result = productService.getAllProducts();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getId());
        assertEquals("Test Guitar", result.getFirst().getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetProductDetails() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(modelMapper.map(product, ProductDetailsDTO.class)).thenReturn(productDetailsDTO);
        when(reviewClient.getReviewsByProductId(1L)).thenReturn(reviews);

        ProductDetailsDTO result = productService.getProductDetails(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Guitar", result.getName());
        assertEquals(4.0, result.getRating());
        assertEquals(1, result.getNumberOfReviews());
        verify(productRepository, times(1)).findById(1L);
        verify(reviewClient, times(1)).getReviewsByProductId(1L);
    }

    @Test
    void testGetProductDetailsNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.getProductDetails(1L));
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testSaveProduct() {
        when(modelMapper.map(productDetailsDTO, Product.class)).thenReturn(product);

        productService.saveProduct(productDetailsDTO);

        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testUpdateProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.updateProduct(productDetailsDTO);

        verify(productRepository, times(1)).save(product);
        verify(modelMapper, times(1)).map(productDetailsDTO, product);
    }

    @Test
    void testUpdateProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.updateProduct(productDetailsDTO));
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testRemoveProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        List<CartItem> cartItems = new ArrayList<>();
        when(cartItemRepository.findByProduct(product)).thenReturn(cartItems);

        List<Order> orders = new ArrayList<>();
        Order order = new Order();
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        order.getOrderItems().add(orderItem);
        orders.add(order);

        when(orderRepository.findByOrderItemsProduct(product)).thenReturn(orders);

        productService.removeProduct(1L);

        verify(cartItemRepository, times(1)).deleteAll(cartItems);
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void testRemoveProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> productService.removeProduct(1L));
        verify(productRepository, times(1)).findById(1L);
    }
}
