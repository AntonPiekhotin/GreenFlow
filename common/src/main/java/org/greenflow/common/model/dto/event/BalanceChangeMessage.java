package org.greenflow.common.model.dto.event;

import lombok.Builder;
import lombok.NonNull;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Builder
public record BalanceChangeMessage(
        @NonNull String userId,
        @NonNull BigDecimal amount,
        String description
) implements Serializable {
    @Serial
    private static final long serialVersionUID = -7123421102348789901L;
}
