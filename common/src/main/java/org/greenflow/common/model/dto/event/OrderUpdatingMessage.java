package org.greenflow.common.model.dto.event;

import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents an event where an order is updated.
 *
 * @param orderId     The unique identifier of the order.
 * @param startDate   The start date of the order.
 * @param description A description of the order update.
 * @param wage        The updated wage for the order.
 */
@Builder
public record OrderUpdatingMessage(
        String orderId,
        LocalDate startDate,
        String description,
        BigDecimal wage
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 5981722452398453061L;
}
