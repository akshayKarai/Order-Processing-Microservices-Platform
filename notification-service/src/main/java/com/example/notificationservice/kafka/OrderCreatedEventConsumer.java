package com.example.notificationservice.kafka;

import com.example.notificationservice.dto.OrderCreatedEvent;
import com.example.notificationservice.entity.Notification;
import com.example.notificationservice.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedEventConsumer.class);

    private final NotificationRepository notificationRepository;

    public OrderCreatedEventConsumer(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @KafkaListener(topics = "order-created", groupId = "notification-service-group")
    public void handleOrderCreated(OrderCreatedEvent event) {

        // Idempotency guard: Kafka can redeliver messages (at-least-once delivery).
        // Without this check, a redelivered event would trigger a duplicate notification.
        if (notificationRepository.findByOrderId(event.getOrderId()).isPresent()) {
            log.info("Notification for orderId={} already sent, skipping duplicate", event.getOrderId());
            return;
        }

        String message = String.format(
                "Hi %s, your order #%d for %d x %s has been received and is being processed.",
                event.getCustomerName(), event.getOrderId(), event.getQuantity(), event.getProductId());

        // In a real system this would call an email/SMS provider (SendGrid, Twilio, SES).
        // For local/demo purposes we log it and persist it so the UI can show a notification feed.
        log.info("Sending notification: {}", message);

        Notification notification = new Notification(event.getOrderId(), event.getCustomerName(), message);
        notificationRepository.save(notification);
    }
}
