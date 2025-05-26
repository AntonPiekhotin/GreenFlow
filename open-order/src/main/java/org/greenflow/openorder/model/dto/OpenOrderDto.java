package org.greenflow.openorder.model.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OpenOrderDto {

    String orderId;
    String clientId;
    String clientEmail;

    double longitude;
    double latitude;

    String description;

    // wage that worker will receive for this order
    BigDecimal wage;

}
