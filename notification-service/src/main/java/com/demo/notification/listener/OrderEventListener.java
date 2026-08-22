package com.demo.notification.listener;

import com.demo.notification.entity.Notification;
import com.demo.notification.event.OrderEvent;
import com.demo.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Mirrors the guide's NotificationConsumer: switches on eventType and logs
 * partition/offset. Persisting a Notification row per event (so
 * GET /api/notifications/user/{id} has something to return) is an addition
 * beyond the guide, which only stubbed out sendEmail(...).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final NotificationRepository notificationRepository;

    @KafkaListener(topics = "order-events", groupId = "notification-group")
    @Transactional
    public void handleOrderEvent(OrderEvent event,
                                  @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                  @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("Received [partition={}, offset={}]: {}", partition, offset, event);

        String message = switch (event.eventType()) {
            case "ORDER_CREATED" -> "Order placed: " + event.orderId();
            case "ORDER_CONFIRMED" -> "Order confirmed!";
            case "ORDER_CANCELLED" -> "Order cancelled.";
            default -> {
                log.warn("Unknown event type: {}", event.eventType());
                yield "Order " + event.orderId() + " updated: " + event.eventType();
            }
        };

        sendEmail(event.userId(), message);

        // Addition beyond the guide: persist so the REST API can serve history.
        Notification notification = Notification.builder()
                .orderId(event.orderId())
                .userId(event.userId())
                .eventType(event.eventType())
                .message(message)
                .build();
        notificationRepository.save(notification);
    }

    private void sendEmail(String userId, String message) {
        // Integrate with AWS SES / SendGrid
        log.info("Sending email to user {} : {}", userId, message);
    }
}
