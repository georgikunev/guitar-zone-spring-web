package com.example.guitarzone.service.impl;

import com.example.guitarzone.ReviewClient;
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
import com.example.guitarzone.service.ProductService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final ReviewClient reviewClient;


    public ProductServiceImpl(ProductRepository productRepository, ModelMapper modelMapper, OrderRepository orderRepository, CartItemRepository cartItemRepository, ReviewClient reviewClient) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.reviewClient = reviewClient;
    }

    @Transactional
    @Override
    public List<ShortProductInfoDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        logger.debug("Fetched products: {}", products);
        return products.stream().map(this::mapToShortInfo).toList();
    }

    @Override
    public ProductDetailsDTO getProductDetails(Long id) {
        ProductDetailsDTO productDetails = productRepository.findById(id).map(this::mapToDetailsInfo).orElseThrow();
        List<ReviewDTO> reviews = reviewClient.getReviewsByProductId(id);
        productDetails.setReviews(reviews);
        productDetails.setNumberOfReviews(reviews.size());

        if (!reviews.isEmpty()) {
            double averageRating = reviews.stream()
                    .mapToInt(ReviewDTO::getRating)
                    .average()
                    .orElse(0.0);
            productDetails.setRating(averageRating);
        } else {
            productDetails.setRating(0.0);
        }

        return productDetails;
    }

    @Override
    public void updateProduct(ProductDetailsDTO productDetailsDTO) {
        Product product = productRepository.findById(productDetailsDTO.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        modelMapper.map(productDetailsDTO, product);
        productRepository.save(product);
    }

    @Override
    public void removeProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // Remove product from all cart items
        List<CartItem> cartItems = cartItemRepository.findByProduct(product);
        cartItemRepository.deleteAll(cartItems);

        // Check if there are any orders associated with the product
        List<Order> associatedOrders = orderRepository.findByOrderItemsProduct(product);
        boolean hasPendingOrders = associatedOrders.stream()
                .anyMatch(order -> "Pending".equals(order.getStatus()));

        if (hasPendingOrders) {
            throw new IllegalStateException("Cannot delete product associated with pending orders");
        }

        // Update related order items
        for (Order order : associatedOrders) {
            for (OrderItem orderItem : order.getOrderItems()) {
                if (orderItem.getProduct() != null && orderItem.getProduct().equals(product)) {
                    orderItem.setProduct(null);  // Set the product reference to null
                }
            }
        }

        productRepository.delete(product);
    }

    @Override
    public void saveProduct(ProductDetailsDTO productDetailsDTO) {
        Product product = modelMapper.map(productDetailsDTO, Product.class);
        productRepository.save(product);
    }

    private ShortProductInfoDTO mapToShortInfo(Product product) {
        return modelMapper.map(product, ShortProductInfoDTO.class);
    }

    private ProductDetailsDTO mapToDetailsInfo(Product product) {
        return modelMapper.map(product, ProductDetailsDTO.class);
    }
}
