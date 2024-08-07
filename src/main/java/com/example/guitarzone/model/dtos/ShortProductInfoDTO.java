package com.example.guitarzone.model.dtos;


public class ShortProductInfoDTO {
    private Long id;
    private String name;
    private String type;
    private Double price;
    private String stockStatus;
    private Double rating;
    private String imageUrl;
    private boolean inWishlist;
    private Integer numberOfReviews;

    public ShortProductInfoDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getStockStatus() {
        return stockStatus;
    }

    public void setStockStatus(String stockStatus) {
        this.stockStatus = stockStatus;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isInWishlist() {
        return inWishlist;
    }
    public void setInWishlist(boolean inWishlist) {
        this.inWishlist = inWishlist;
    }

    public Integer getNumberOfReviews() {
        return numberOfReviews;
    }
    public void setNumberOfReviews(Integer numberOfReviews) {
        this.numberOfReviews = numberOfReviews;
    }
}
