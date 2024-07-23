package com.example.guitarzone.model.dtos;

import java.util.List;

public class CartDTO {
    private List<CartItemDTO> items;
    private Double total;

    public CartDTO() {
    }

    public List<CartItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
