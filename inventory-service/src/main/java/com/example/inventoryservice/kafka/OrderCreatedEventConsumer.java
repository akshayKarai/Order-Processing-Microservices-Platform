package com.example.inventoryservice.kafka;

import com.example.inventoryservice.dto.OrderCreatedEvent;
import com.example.inventoryservice.dto.OrderStatusEvent;
import com.example.inventoryservice.entity.Product;
import com.example.inventoryservice.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrderCreatedEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedEventConsumer.class);

    private final ProductRepository productRepository;
    private final OrderStatusEventProducer statusEventProducer;

    public OrderCreatedEventConsumer(ProductRepository productRepository,
                                      OrderStatusEventProducer statusEventProducer) {
        this.productRepository = productRepository;
        this.statusEventProducer = statusEventProducer;
    }

    @KafkaListener(topics = "order-created", groupId = "inventory-service-group")
    @Transactional
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Received order-created event for orderId={} productId={} qty={}",
                event.getOrderId(), event.getProductId(), event.getQuantity());

        Product product = productRepository.findById(event.getProductId()).orElse(null);

        if (product == null) {
            log.warn("Product {} not found, rejecting order {}", event.getProductId(), event.getOrderId());
            statusEventProducer.publish(new OrderStatusEvent(event.getOrderId(), "REJECTED", "Product not found"));
            return;
        }

        if (product.getStockQuantity() < event.getQuantity()) {
            log.warn("Insufficient stock for product {} (have {}, need {}), rejecting order {}",
                    event.getProductId(), product.getStockQuantity(), event.getQuantity(), event.getOrderId());
            statusEventProducer.publish(new OrderStatusEvent(event.getOrderId(), "REJECTED", "Insufficient stock"));
            return;
        }

        product.setStockQuantity(product.getStockQuantity() - event.getQuantity());
        productRepository.save(product);
        log.info("Stock decremented for product {}: {} remaining", product.getProductId(), product.getStockQuantity());

        statusEventProducer.publish(new OrderStatusEvent(event.getOrderId(), "CONFIRMED", null));
    }
}
