package com.example.guitarzone.controllers;

import com.example.guitarzone.model.dtos.ReviewDTO;
import com.example.guitarzone.service.ReviewService;
import com.example.guitarzone.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.Instant;

@Controller
@RequestMapping("/guitars")
public class ReviewController {
    private final ReviewService reviewService;
    private final UserService userService;

    public ReviewController(ReviewService reviewService, UserService userService) {
        this.reviewService = reviewService;
        this.userService = userService;
    }

    @PostMapping("/{productId}/reviews/add")
    public String addReview(@PathVariable("productId") Long productId,
                            @RequestParam int rating,
                            @RequestParam String comment,
                            Principal principal,
                            RedirectAttributes redirectAttributes) {
        Long userId = userService.findByEmail(principal.getName()).getId();
        if (reviewService.hasUserReviewedProduct(userId, productId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You have already reviewed this product.");
            return "redirect:/guitars/guitar-details/" + productId;
        }

        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setUserId(userId);
        reviewDTO.setProductId(productId);
        reviewDTO.setRating(rating);
        reviewDTO.setComment(comment);
        reviewDTO.setCreatedDate(Instant.now());

        reviewService.addReview(reviewDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Review submitted successfully.");

        return "redirect:/guitars/guitar-details/" + productId;
    }
}