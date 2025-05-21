package org.greenflow.common.model.dto.event;

import lombok.Builder;
import lombok.NonNull;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Builder
public record PaymentCreationMessage(
        @NonNull String userId,
        @NonNull BigDecimal amount,
        @NonNull String currency,
        String description
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 4129087512359812352L;
}
