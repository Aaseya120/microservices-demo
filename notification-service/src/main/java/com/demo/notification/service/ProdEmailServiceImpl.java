package com.demo.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
@Slf4j
public class ProdEmailServiceImpl implements EmailService {
    @Override
    public void sendEmail(String userId, String message) {
        // Integrate with real AWS SES / SendGrid etc.
        log.info("[PROD EMAIL] Sending real email to user {}: {}", userId, message);
    }
}
