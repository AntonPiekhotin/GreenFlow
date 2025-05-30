package org.greenflow.common.model.dto.event;

import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents an email notification event.
 *
 * @param userId  The unique identifier of the user.
 * @param subject The subject of the email.
 * @param text    The text content of the email.
 */
@Builder
public record EmailNotificationMessage(String userId,
                                       String subject,
                                       String text) implements Serializable {
    @Serial
    private static final long serialVersionUID = 7491237559128751231L;
}
