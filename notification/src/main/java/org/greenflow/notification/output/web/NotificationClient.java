package org.greenflow.notification.output.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.common.model.exception.GreenFlowException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationClient {

    private final RestTemplate restTemplate;

    @Value("${api.host.auth-service}")
    private String AUTH_SERVICE_HOST;

    public String getClientEmailFromAuthService(String userId) {
        log.debug("Fetching email for user ID: {}", userId);
        String url = String.format("http://%s/api/v1/auth/email?userId=%s", AUTH_SERVICE_HOST, userId);
        try {
            String email = restTemplate.getForObject(url, String.class);
            log.debug("Fetched email: {}", email);
            if (email == null || email.isEmpty()) {
                log.warn("No email found for user ID: {}", userId);
                throw new GreenFlowException(400, "Email not found for user ID: " + userId);
            }
            return email;
        } catch (Exception e) {
            log.error("Failed to fetch email for user ID {}: {}", userId, e.getMessage());
            throw new GreenFlowException(503, "Failed to fetch email", e);
        }
    }
}
