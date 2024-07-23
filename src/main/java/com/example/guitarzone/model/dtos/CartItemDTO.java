package com.example.guitarzone.model.dtos;

public class CartItemDTO {
    private Long productId;
    private ProductInCartDTO product;
    private Double price;
    private Integer quantity;
    private Double subtotal;

    public CartItemDTO() {
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public ProductInCartDTO getProduct() {
        return product;
    }

    public void setProduct(ProductInCartDTO product) {
        this.product = product;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }
}
