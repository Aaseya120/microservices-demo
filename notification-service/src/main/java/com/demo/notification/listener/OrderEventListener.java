package com.demo.notification.listener;

import com.demo.notification.entity.Notification;
import com.demo.notification.saga.command.SendNotificationCommand;
import com.demo.notification.repository.NotificationRepository;
import com.demo.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @RetryableTopic(attempts = "3", dltStrategy = org.springframework.kafka.retrytopic.DltStrategy.FAIL_ON_ERROR)
    @KafkaListener(topics = "notification-commands", groupId = "notification-group")
    @Transactional
    public void handleNotificationCommand(SendNotificationCommand command,
                                  @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                  @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("Received [partition={}, offset={}]: {}", partition, offset, command);

        Object messageBody = command.message() != null ? command.message() : "UNKNOWN";
        
        switch (messageBody) {
            case String s when s.contains("inventory") -> emailService.sendEmail(command.userId(), "Inventory reserved for your order.");
            case String s when s.contains("sent") -> emailService.sendEmail(command.userId(), command.message());
            case String s -> emailService.sendEmail(command.userId(), command.message());
            default -> log.warn("Unknown message format");
        }

        var notification = Notification.builder()
                .orderId(command.orderId())
                .userId(command.userId())
                .eventType("NOTIFICATION_SENT")
                .message(command.message())
                .build();
        notificationRepository.save(notification);
    }
}
