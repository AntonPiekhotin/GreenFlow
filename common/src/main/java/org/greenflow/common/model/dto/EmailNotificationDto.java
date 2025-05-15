package org.greenflow.common.model.dto;

import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;

@Builder
public record EmailNotificationDto(String to,
                                   String subject,
                                   String text) implements Serializable {
    @Serial
    private static final long serialVersionUID = 7491237559128751231L;
}
