package com.demo.order.saga;

import com.demo.order.entity.Order;
import com.demo.order.entity.OrderStatus;
import com.demo.order.entity.OutboxEvent;
import com.demo.order.repository.OrderRepository;
import com.demo.order.repository.OutboxEventRepository;
import com.demo.order.saga.command.SendNotificationCommand;
import com.demo.order.saga.reply.InventoryReply;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderSagaOrchestrator {

    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "product-replies", groupId = "order-group")
    @Transactional
    @SneakyThrows
    public void handleProductReply(InventoryReply reply) {
        log.info("Received InventoryReply: {}", reply);

        Order order = orderRepository.findById(reply.orderId()).orElse(null);
        if (order == null) {
            log.error("Order not found for ID: {}", reply.orderId());
            return;
        }

        String message;
        if ("RESERVED".equals(reply.status())) {
            order.setStatus(OrderStatus.CONFIRMED);
            message = "Your order " + order.getId() + " has been confirmed and inventory is reserved.";
        } else {
            order.setStatus(OrderStatus.CANCELLED);
            message = "Your order " + order.getId() + " was cancelled. Reason: " + reply.reason();
        }

        orderRepository.save(order);

        // Send a notification command
        SendNotificationCommand notifyCommand = new SendNotificationCommand(order.getId(), order.getUserId(), message);
        OutboxEvent outboxEvent = OutboxEvent.builder()
                .aggregateId(order.getId())
                .aggregateType("Order")
                .eventType("SEND_NOTIFICATION")
                .payload(objectMapper.writeValueAsString(notifyCommand))
                .build();
        outboxEventRepository.save(outboxEvent);
    }
}
