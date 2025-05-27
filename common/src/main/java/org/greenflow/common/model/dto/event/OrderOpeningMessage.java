package org.greenflow.common.model.dto.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderOpeningMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1131332095712355123L;

    String orderId;
    String clientId;
    String clientEmail;

    double longitude;
    double latitude;

    String description;

    // wage that worker will receive for this order
    BigDecimal wage;
}
