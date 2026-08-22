package com.demo.notification.listener;

import com.demo.notification.entity.Notification;
import com.demo.notification.saga.command.SendNotificationCommand;
import com.demo.notification.repository.NotificationRepository;
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
public class OrderEventListener { // Kept name for file consistency, but acts as command handler

    private final NotificationRepository notificationRepository;

    @RetryableTopic(attempts = "3", dltStrategy = org.springframework.kafka.retrytopic.DltStrategy.FAIL_ON_ERROR)
    @KafkaListener(topics = "notification-commands", groupId = "notification-group")
    @Transactional
    public void handleNotificationCommand(SendNotificationCommand command,
                                  @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                  @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("Received [partition={}, offset={}]: {}", partition, offset, command);

        sendEmail(command.userId(), command.message());

        Notification notification = Notification.builder()
                .orderId(command.orderId())
                .userId(command.userId())
                .eventType("NOTIFICATION_SENT")
                .message(command.message())
                .build();
        notificationRepository.save(notification);
    }

    private void sendEmail(String userId, String message) {
        // Integrate with AWS SES / SendGrid
        log.info("Sending email to user {} : {}", userId, message);
    }
}
