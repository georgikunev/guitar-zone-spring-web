package com.example.guitarzone;

import com.example.guitarzone.model.dtos.CartDTO;
import com.example.guitarzone.model.entities.Cart;
import com.example.guitarzone.model.entities.CartItem;
import com.example.guitarzone.model.entities.Product;
import com.example.guitarzone.model.entities.User;
import com.example.guitarzone.repositories.CartRepository;
import com.example.guitarzone.repositories.ProductRepository;
import com.example.guitarzone.repositories.UserRepository;
import com.example.guitarzone.service.impl.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    private User user;
    private Product product;
    private Cart cart;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(100.0);
        product.setQuantity(10);

        cart = new Cart();
        cart.setUser(user);
        cart.setItems(new ArrayList<>());
        cart.setTotal(0.0);
    }

    @Test
    void testGetCartByUserId_UserExists() {
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));

        CartDTO cartDTO = cartService.getCartByUserId(user.getId());

        assertNotNull(cartDTO);
        verify(cartRepository, times(1)).findByUserId(user.getId());
    }

    @Test
    void testGetCartByUserId_UserDoesNotExist() {
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.empty());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartDTO cartDTO = cartService.getCartByUserId(user.getId());

        assertNotNull(cartDTO);
        verify(userRepository, times(1)).findById(user.getId());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testAddItemToCart_ProductAlreadyInCart() {
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setPrice(100.0); // Set the price for CartItem
        cart.setItems(Collections.singletonList(cartItem));

        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        String result = cartService.addItemToCart(user.getId(), product.getId(), 1);

        assertEquals("Product already in cart", result);
    }

    @Test
    void testAddItemToCart_NewProduct() {
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = cartService.addItemToCart(user.getId(), product.getId(), 1);

        assertEquals("Product added to cart", result);
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void testRemoveItemFromCart() {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setProduct(product);
        cartItem.setPrice(100.0); // Set the price for CartItem
        cart.setItems(new ArrayList<>(Collections.singletonList(cartItem))); // Use a modifiable list

        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));

        cartService.removeItemFromCart(user.getId(), cartItem.getId());

        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void testIncrementCartItem() {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setProduct(product);
        cartItem.setQuantity(1);
        cartItem.setPrice(100.0); // Set the price for CartItem
        cart.setItems(Collections.singletonList(cartItem));

        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        cartService.incrementCartItem(user.getId(), cartItem.getId());

        assertEquals(2, cartItem.getQuantity());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void testDecrementCartItem() {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setPrice(100.0); // Set the price for CartItem
        cart.setItems(Collections.singletonList(cartItem));

        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        cartService.decrementCartItem(user.getId(), cartItem.getId());

        assertEquals(1, cartItem.getQuantity());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void testClearCart() {
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        cartService.clearCart(user.getId());

        assertTrue(cart.getItems().isEmpty());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void testIsCartEmpty() {
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));

        boolean isEmpty = cartService.isCartEmpty(user.getId());

        assertTrue(isEmpty);
    }
}
