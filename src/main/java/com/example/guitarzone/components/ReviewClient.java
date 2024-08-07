package com.example.guitarzone.components;

import com.example.guitarzone.model.dtos.ReviewDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;


import java.util.Arrays;
import java.util.List;

@Component
public class ReviewClient {

    private final RestClient restClient;

    @Value("${guitarzone-reviews.url}")
    private String reviewServiceUrl;

    public ReviewClient(RestClient restClient) {
        this.restClient = restClient;
    }


    public void addReview(ReviewDTO reviewDTO) {
        String url = UriComponentsBuilder.fromHttpUrl(reviewServiceUrl).path("/reviews").toUriString();
        try {
            restClient.post()
                    .uri(url)
                    .body(reviewDTO)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            throw new IllegalArgumentException("Could not add review.");
        }
    }

    public List<ReviewDTO> getReviewsByProductId(Long productId) {
        String url = UriComponentsBuilder.fromHttpUrl(reviewServiceUrl).path("/reviews/{productId}").buildAndExpand(productId).toUriString();
        ReviewDTO[] response = restClient.get()
                .uri(url)
                .retrieve()
                .body(ReviewDTO[].class);
        return Arrays.asList(response);
    }

    public boolean hasUserReviewedProduct(Long userId, Long productId) {
        String url = UriComponentsBuilder.fromHttpUrl(reviewServiceUrl)
                .path("/reviews/exists")
                .queryParam("userId", userId)
                .queryParam("productId", productId)
                .toUriString();

        return restClient.get()
                .uri(url)
                .retrieve()
                .body(Boolean.class);
    }
    public void deleteReviewsByProductId(Long productId) {
        String url = UriComponentsBuilder.fromHttpUrl(reviewServiceUrl)
                .path("/reviews/product/{productId}")
                .buildAndExpand(productId)
                .toUriString();

        try {
            restClient.delete()
                    .uri(url)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            throw new IllegalArgumentException("Could not delete reviews.");
        }
    }
}