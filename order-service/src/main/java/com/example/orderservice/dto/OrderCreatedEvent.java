package com.example.orderservice.dto;

public class OrderCreatedEvent {
    private Long orderId;
    private String productId;
    private Integer quantity;
    private String customerName;

    public OrderCreatedEvent() {}

    public OrderCreatedEvent(Long orderId, String productId, Integer quantity, String customerName) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.customerName = customerName;
    }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
}
