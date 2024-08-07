package com.example.guitarzone;

import com.example.guitarzone.model.entities.Product;
import com.example.guitarzone.model.entities.User;
import com.example.guitarzone.repositories.ProductRepository;
import com.example.guitarzone.repositories.UserRepository;
import com.example.guitarzone.service.impl.WishlistServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WishlistServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private WishlistServiceImpl wishlistService;

    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setPassword("password");

        product = new Product();
        product.setId(1L);
        product.setName("Guitar");
    }

    @Test
    void testGetWishlistItems() {
        user.setWishlist(Collections.singletonList(product));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        List<Product> wishlistItems = wishlistService.getWishlistItems(user.getId());

        assertNotNull(wishlistItems);
        assertEquals(1, wishlistItems.size());
        assertEquals(product.getName(), wishlistItems.get(0).getName());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void testAddToWishlist() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        boolean result = wishlistService.addToWishlist(user.getId(), product.getId());

        assertTrue(result);
        assertTrue(user.getWishlist().contains(product));
        verify(userRepository, times(1)).findById(user.getId());
        verify(productRepository, times(1)).findById(product.getId());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testAddToWishlist_ProductAlreadyInWishlist() {
        user.setWishlist(Collections.singletonList(product));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        boolean result = wishlistService.addToWishlist(user.getId(), product.getId());

        assertFalse(result);
        verify(userRepository, times(1)).findById(user.getId());
        verify(productRepository, times(1)).findById(product.getId());
        verify(userRepository, times(0)).save(user);
    }

    @Test
    void testRemoveFromWishlist() {
        // Use a mutable list
        user.setWishlist(new ArrayList<>(Collections.singletonList(product)));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        wishlistService.removeFromWishlist(user.getId(), product.getId());

        assertFalse(user.getWishlist().contains(product));
        verify(userRepository, times(1)).findById(user.getId());
        verify(productRepository, times(1)).findById(product.getId());
        verify(userRepository, times(1)).save(user);
    }
    @Test
    void testClearWishlist() {
        user.setWishlist(new ArrayList<>(Collections.singletonList(product)));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        wishlistService.clearWishlist(user.getId());

        assertTrue(user.getWishlist().isEmpty());
        verify(userRepository, times(1)).findById(user.getId());
        verify(userRepository, times(1)).save(user);
    }
}
