package com.demo.order.service;

import com.demo.order.dto.Dtos.OrderRequest;
import com.demo.order.entity.Order;
import com.demo.order.exception.OrderNotFoundException;
import com.demo.order.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.demo.order.entity.OutboxEvent;
import com.demo.order.saga.command.ReserveInventoryCommand;
import com.demo.order.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;
    private final com.demo.order.client.ProductClient productClient;

    @Transactional
    @SneakyThrows
    public Order createOrder(OrderRequest req) {
        var product = productClient.getProduct(req.productId());
        var calculatedPrice = product.price().multiply(new BigDecimal(req.qty()));

        var order = Order.builder()
                .userId(req.userId())
                .productId(req.productId())
                .quantity(req.qty())
                .totalPrice(calculatedPrice)
                .build();

        var savedOrder = orderRepository.save(order);

        var command = new ReserveInventoryCommand(savedOrder.getId(), savedOrder.getProductId(), savedOrder.getQuantity());
        OutboxEvent outboxEvent = OutboxEvent.builder()
                .aggregateId(order.getId())
                .aggregateType("Order")
                .eventType("RESERVE_INVENTORY")
                .payload(objectMapper.writeValueAsString(command))
                .build();

        outboxEventRepository.save(outboxEvent);

        return savedOrder;
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
