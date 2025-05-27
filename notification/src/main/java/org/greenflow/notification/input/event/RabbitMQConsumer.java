package org.greenflow.notification.input.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.common.model.constant.RabbitMQConstants;
import org.greenflow.common.model.dto.event.EmailNotificationMessage;
import org.greenflow.notification.service.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQConsumer {

    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConstants.NOTIFICATION_QUEUE)
    public void consumeOrderOpeningMessage(EmailNotificationMessage email) {
        log.info("Received email notification message to: {}", email.userId());
        try {
            emailService.sendEmail(email);
        } catch (Exception e) {
            log.error("Error sending email notification: {}", e.getMessage());
        }
    }
}

