package org.greenflow.common.model.dto.event;

import lombok.Builder;
import lombok.NonNull;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Represents a balance change event for a user.
 *
 * @param userId      The unique identifier of the user.
 * @param amount      The amount of the balance change.
 * @param description An optional description of the balance change.
 */
@Builder
public record BalanceChangeMessage(
        @NonNull String userId,
        @NonNull BigDecimal amount,
        String description
) implements Serializable {
    @Serial
    private static final long serialVersionUID = -7123421102348789901L;
}
