package com.example.orderservice.kafka;

import com.example.orderservice.dto.OrderStatusEvent;
import com.example.orderservice.entity.Order;
import com.example.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderStatusEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderStatusEventConsumer.class);

    private final OrderRepository orderRepository;

    public OrderStatusEventConsumer(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @KafkaListener(topics = "order-status-updated", groupId = "order-service-group")
    public void handleOrderStatusUpdate(OrderStatusEvent event) {
        log.info("Received status update for orderId={} status={}", event.getOrderId(), event.getStatus());
        orderRepository.findById(event.getOrderId()).ifPresentOrElse(order -> {
            order.setStatus(event.getStatus());
            orderRepository.save(order);
            log.info("Order {} updated to {}", order.getId(), event.getStatus());
        }, () -> log.warn("Received status update for unknown orderId={}", event.getOrderId()));
    }
}
