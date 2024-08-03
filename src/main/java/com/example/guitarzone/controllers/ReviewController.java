package com.example.guitarzone.controllers;

import com.example.guitarzone.ReviewClient;
import com.example.guitarzone.model.dtos.ReviewDTO;
import com.example.guitarzone.model.entities.User;
import com.example.guitarzone.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.Instant;

@Controller
@RequestMapping("/guitars")
public class ReviewController {
    private final ReviewClient reviewClient;
    private final UserService userService;

    public ReviewController(ReviewClient reviewClient, UserService userService) {
        this.reviewClient = reviewClient;
        this.userService = userService;
    }


    @PostMapping("/{productId}/reviews/add")
    public String addReview(@PathVariable("productId") Long productId,
                            @RequestParam int rating,
                            @RequestParam String comment,
                            Principal principal,
                            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(principal.getName());
        Long userId = userService.findByEmail(principal.getName()).getId();
        if (reviewClient.hasUserReviewedProduct(userId, productId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You have already reviewed this product.");
            return "redirect:/guitars/guitar-details/" + productId;
        }

        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setUserId(userId);
        reviewDTO.setProductId(productId);
        reviewDTO.setFullName(user.getFullName());
        reviewDTO.setRating(rating);
        reviewDTO.setComment(comment);
        reviewDTO.setCreatedDate(Instant.now());

        reviewClient.addReview(reviewDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Review submitted successfully.");

        return "redirect:/guitars/guitar-details/" + productId;
    }
}