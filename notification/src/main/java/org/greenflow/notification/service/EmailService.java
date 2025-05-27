package org.greenflow.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.common.model.dto.event.EmailNotificationMessage;
import org.greenflow.notification.output.web.NotificationClient;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final NotificationClient notificationClient;

    public void sendEmail(EmailNotificationMessage email) {
        log.debug("Sending email to: {}", email.userId());
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(getEmail(email.userId()));
        message.setSubject(email.subject());
        message.setText(email.text());
        mailSender.send(message);
        log.debug("Email sent to: {}", email.userId());
    }

    private String getEmail(String userId) {
        log.info("Fetching email for user ID: {}", userId);
        String email = notificationClient.getClientEmailFromAuthService(userId);
        log.debug("Fetched email: {}", email);
        return email;
    }

}
