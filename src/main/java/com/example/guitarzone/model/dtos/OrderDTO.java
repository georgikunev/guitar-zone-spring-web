package com.example.guitarzone.model.dtos;

import com.example.guitarzone.model.embeddable.Address;
import com.example.guitarzone.model.embeddable.PaymentDetails;

import java.util.List;

public class OrderDTO {
    private Address shippingAddress;
    private PaymentDetails paymentDetails;
    private Double totalAmount;
    private List<OrderItemDTO> orderItems;

    public OrderDTO() {
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public PaymentDetails getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(PaymentDetails paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }
}
