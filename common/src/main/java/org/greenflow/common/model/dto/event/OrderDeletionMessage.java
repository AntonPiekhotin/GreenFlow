package org.greenflow.common.model.dto.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents an event where an order is deleted.
 *
 * @param orderId The unique identifier of the order.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDeletionMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 8913804712530981234L;

    String orderId;

}
