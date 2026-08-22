package com.demo.order.service;

import com.demo.order.entity.OutboxEvent;
import com.demo.order.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxPoller {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void pollOutbox() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findByProcessedFalseOrderByCreatedAtAsc();
        if (!pendingEvents.isEmpty()) {
            log.info("Found {} pending outbox events", pendingEvents.size());
            for (OutboxEvent event : pendingEvents) {
                try {
                    // We map the eventType to topics for this simple demo
                    String topic = switch (event.getEventType()) {
                        case "RESERVE_INVENTORY" -> "product-commands";
                        case "SEND_NOTIFICATION" -> "notification-commands";
                        default -> "order-events";
                    };

                    kafkaTemplate.send(topic, event.getAggregateId(), event.getPayload());
                    event.setProcessed(true);
                    outboxEventRepository.save(event);
                } catch (Exception e) {
                    log.error("Failed to process outbox event: {}", event.getId(), e);
                    // Will be retried on next poll
                }
            }
        }
    }
}
