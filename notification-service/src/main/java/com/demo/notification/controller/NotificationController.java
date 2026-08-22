package com.demo.notification.controller;

import com.demo.notification.entity.Notification;
import com.demo.notification.repository.NotificationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notification API", description = "Operations related to notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get notifications for a user (paginated)")
    public ResponseEntity<Page<Notification>> getForUser(@PathVariable String userId, Pageable pageable) {
        log.info("Fetching notifications for user: {}", userId);
        return ResponseEntity.ok(notificationRepository.findByUserId(userId, pageable));
    }

    @GetMapping
    @Operation(summary = "Get all notifications (paginated)")
    public ResponseEntity<Page<Notification>> getAll(Pageable pageable) {
        log.info("Fetching all notifications");
        return ResponseEntity.ok(notificationRepository.findAll(pageable));
    }
}
