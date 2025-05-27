package org.greenflow.common.model.dto.event;

import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;

@Builder
public record EmailNotificationMessage(String userId,
                                       String subject,
                                       String text) implements Serializable {
    @Serial
    private static final long serialVersionUID = 7491237559128751231L;
}
