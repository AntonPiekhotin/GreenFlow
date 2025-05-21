package org.greenflow.common.model.dto.event;

import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;

@Builder
public record PaymentCreationMessage(
        @NonNull String userId,
        @NonNull BigDecimal amount,
        @NonNull String currency,
        String description
) {
}
