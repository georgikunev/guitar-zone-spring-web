package com.example.guitarzone.service.impl;

import com.example.guitarzone.model.dtos.CartDTO;
import com.example.guitarzone.model.dtos.CartItemDTO;
import com.example.guitarzone.model.dtos.ProductInCartDTO;
import com.example.guitarzone.model.entities.Cart;
import com.example.guitarzone.model.entities.CartItem;
import com.example.guitarzone.model.entities.Product;
import com.example.guitarzone.model.entities.User;
import com.example.guitarzone.repositories.CartRepository;
import com.example.guitarzone.repositories.ProductRepository;
import com.example.guitarzone.repositories.UserRepository;
import com.example.guitarzone.service.CartService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public CartServiceImpl(CartRepository cartRepository, UserRepository userRepository, ProductRepository productRepository, ModelMapper modelMapper) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CartDTO getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });

        return mapToDTO(cart);
    }

    @Override
    public void addItemToCart(Long userId, Long productId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });

        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            CartItem item = new CartItem();
            item.setProduct(product);
            item.setPrice(product.getPrice());
            item.setQuantity(quantity);
            item.setCart(cart);
            cart.getItems().add(item);
        }

        updateCartTotal(cart);
        cartRepository.save(cart);
    }

    @Override
    public void updateCartItemQuantity(Long userId, Long itemId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("Cart not found"));
        CartItem item = cart.getItems().stream()
                .filter(cartItem -> cartItem.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("CartItem not found"));

        item.setQuantity(quantity);
        updateCartTotal(cart);
        cartRepository.save(cart);
    }

    @Override
    public void removeItemFromCart(Long userId, Long itemId) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("Cart not found"));
        CartItem item = cart.getItems().stream()
                .filter(cartItem -> cartItem.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("CartItem not found"));

        cart.getItems().remove(item);
        item.setCart(null);
        updateCartTotal(cart);
        cartRepository.save(cart);
    }

    private void updateCartTotal(Cart cart) {
        double total = cart.getItems().stream().mapToDouble(CartItem::getSubtotal).sum();
        cart.setTotal(total);
    }

    private CartDTO mapToDTO(Cart cart) {
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        cartDTO.setItems(cart.getItems().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList()));
        return cartDTO;
    }

    private CartItemDTO mapToDTO(CartItem item) {
        CartItemDTO cartItemDTO = modelMapper.map(item, CartItemDTO.class);
        cartItemDTO.setProduct(modelMapper.map(item.getProduct(), ProductInCartDTO.class));
        return cartItemDTO;
    }
}
