package com.demo.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!prod")
@Slf4j
public class MockEmailServiceImpl implements EmailService {
    @Override
    public void sendEmail(String userId, String message) {
        log.info("[MOCK EMAIL] Sending email to user {}: {}", userId, message);
    }
}
