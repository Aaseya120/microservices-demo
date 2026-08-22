package com.demo.order.service;

import com.demo.order.dto.Dtos.OrderRequest;
import com.demo.order.entity.Order;
import com.demo.order.exception.OrderNotFoundException;
import com.demo.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    public Order createOrder(OrderRequest req) {
        Order order = Order.builder()
                .userId(req.userId())
                .productId(req.productId())
                .quantity(req.qty())
                .totalPrice(new BigDecimal(req.qty() * 100)) // Mock price calculation
                .build();

        order = orderRepository.save(order);
        kafkaTemplate.send("order-events", order.getId());
        return order;
    }

    @Transactional(readOnly = true)
    public Order getOrder(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Order> getByUser(String userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable);
    }
}
