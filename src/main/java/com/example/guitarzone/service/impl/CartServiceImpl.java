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
        Cart cart = getCartEntityByUserId(userId);
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
    public void removeItemFromCart(Long userId, Long itemId) {
        Cart cart = getCartEntityByUserId(userId);
        CartItem item = findCartItemById(cart, itemId);

        cart.getItems().remove(item);
        item.setCart(null);
        updateCartTotal(cart);
        cartRepository.save(cart);
    }

    @Override
    public void incrementCartItem(Long userId, Long itemId) {
        Cart cart = getCartEntityByUserId(userId);
        CartItem item = findCartItemById(cart, itemId);

        item.setQuantity(item.getQuantity() + 1);
        updateCartTotal(cart);
        cartRepository.save(cart);
    }

    @Override
    public void decrementCartItem(Long userId, Long itemId) {
        Cart cart = getCartEntityByUserId(userId);
        CartItem item = findCartItemById(cart, itemId);

        if (item.getQuantity() > 1) {
            item.setQuantity(item.getQuantity() - 1);
            updateCartTotal(cart);
            cartRepository.save(cart);
        } else {
            removeItemFromCart(userId, itemId);
        }
    }

    @Override
    public void clearCart(Long userId) {
        Cart cart = getCartEntityByUserId(userId);
        cart.getItems().clear();
        updateCartTotal(cart);
        cartRepository.save(cart);
    }

    private Cart getCartEntityByUserId(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });
    }

    private CartItem findCartItemById(Cart cart, Long itemId) {
        return cart.getItems().stream()
                .filter(cartItem -> cartItem.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("CartItem not found"));
    }

    private void updateCartTotal(Cart cart) {
        double total = cart.getItems().stream().mapToDouble(CartItem::getSubtotal).sum();
        cart.setTotal(total);
    }

    private CartDTO mapToDTO(Cart cart) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setTotal(cart.getTotal());
        cartDTO.setItems(cart.getItems().stream().map(this::mapToDTO).collect(Collectors.toList()));
        return cartDTO;
    }

    private CartItemDTO mapToDTO(CartItem item) {
        CartItemDTO itemDTO = new CartItemDTO();
        itemDTO.setProductId(item.getId());
        itemDTO.setProduct(modelMapper.map(item.getProduct(), ProductInCartDTO.class));
        itemDTO.setPrice(item.getPrice());
        itemDTO.setQuantity(item.getQuantity());
        itemDTO.setSubtotal(item.getSubtotal());
        return itemDTO;
    }
}
