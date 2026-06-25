package com.example.orderservice.service;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderCreatedEvent;
import com.example.orderservice.entity.Order;
import com.example.orderservice.kafka.OrderEventProducer;
import com.example.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;

    public OrderService(OrderRepository orderRepository, OrderEventProducer orderEventProducer) {
        this.orderRepository = orderRepository;
        this.orderEventProducer = orderEventProducer;
    }

    public Order placeOrder(CreateOrderRequest request) {
        Order order = new Order(request.getProductId(), request.getQuantity(), request.getCustomerName());
        Order saved = orderRepository.save(order);
        log.info("Order {} saved as PENDING", saved.getId());

        OrderCreatedEvent event = new OrderCreatedEvent(
                saved.getId(), saved.getProductId(), saved.getQuantity(), saved.getCustomerName());
        orderEventProducer.publishOrderCreated(event);

        return saved;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrder(Long id) {
        return orderRepository.findById(id);
    }
}
