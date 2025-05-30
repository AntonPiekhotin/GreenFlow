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

/**
 * Represents an event where an order is opened.
 *
 * @param orderId     The unique identifier of the order.
 * @param clientId    The unique identifier of the client.
 * @param clientEmail The email address of the client.
 * @param longitude   The longitude of the order location.
 * @param latitude    The latitude of the order location.
 * @param description A description of the order.
 * @param wage        The wage that the worker will receive for this order.
 */
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
