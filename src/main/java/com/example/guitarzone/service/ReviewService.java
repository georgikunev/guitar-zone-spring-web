package com.example.guitarzone.service;

import com.example.guitarzone.model.dtos.ReviewDTO;

import java.util.List;

public interface ReviewService {
    void addReview(ReviewDTO reviewDTO);
    List<ReviewDTO> getReviewsByProductId(Long productId);
    boolean hasUserReviewedProduct(Long userId, Long productId);
}
