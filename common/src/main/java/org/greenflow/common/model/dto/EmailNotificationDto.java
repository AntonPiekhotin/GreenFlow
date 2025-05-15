package org.greenflow.common.model.dto;

import lombok.Builder;

@Builder
public record EmailNotificationDto(String to,
                                   String subject,
                                   String text) {
}
