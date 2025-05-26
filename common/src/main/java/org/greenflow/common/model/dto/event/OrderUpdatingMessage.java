package org.greenflow.common.model.dto.event;

import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

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
