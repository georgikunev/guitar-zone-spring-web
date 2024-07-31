package com.example.guitarzone.service.impl;

import com.example.guitarzone.model.dtos.ReviewDTO;
import com.example.guitarzone.model.entities.Product;
import com.example.guitarzone.model.entities.Review;
import com.example.guitarzone.model.entities.User;
import com.example.guitarzone.repositories.ProductRepository;
import com.example.guitarzone.repositories.ReviewRepository;
import com.example.guitarzone.repositories.UserRepository;
import com.example.guitarzone.service.ReviewService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public ReviewServiceImpl(ReviewRepository reviewRepository, ProductRepository productRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean hasUserReviewedProduct(Long userId, Long productId) {
        return reviewRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Override
    public void addReview(ReviewDTO reviewDTO) {
        Review review = modelMapper.map(reviewDTO, Review.class);
        Product product = productRepository.findById(reviewDTO.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));
        User user = userRepository.findById(reviewDTO.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));

        review.setProduct(product);
        review.setUser(user);
        review.setCreatedDate(Instant.now());

        reviewRepository.save(review);
    }

    @Override
    public List<ReviewDTO> getReviewsByProductId(Long productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);
        return reviews.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private ReviewDTO mapToDTO(Review review) {
        ReviewDTO reviewDTO = modelMapper.map(review, ReviewDTO.class);
        reviewDTO.setUsername(review.getUser().getFirstName() + " " + review.getUser().getLastName());
        return reviewDTO;
    }
}

