package com.example.notificationservice.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "notifications", uniqueConstraints = @UniqueConstraint(columnNames = "orderId"))
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private Instant sentAt;

    public Notification() {}

    public Notification(Long orderId, String customerName, String message) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.message = message;
        this.sentAt = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Instant getSentAt() { return sentAt; }
    public void setSentAt(Instant sentAt) { this.sentAt = sentAt; }
}
