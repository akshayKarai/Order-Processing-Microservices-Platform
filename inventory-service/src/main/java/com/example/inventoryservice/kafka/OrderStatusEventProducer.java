package com.example.inventoryservice.kafka;

import com.example.inventoryservice.dto.OrderStatusEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderStatusEventProducer {

    private static final Logger log = LoggerFactory.getLogger(OrderStatusEventProducer.class);
    public static final String ORDER_STATUS_TOPIC = "order-status-updated";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderStatusEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(OrderStatusEvent event) {
        log.info("Publishing order status update orderId={} status={}", event.getOrderId(), event.getStatus());
        kafkaTemplate.send(ORDER_STATUS_TOPIC, event.getOrderId().toString(), event);
    }
}
