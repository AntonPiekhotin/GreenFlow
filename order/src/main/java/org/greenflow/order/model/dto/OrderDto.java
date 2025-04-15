package org.greenflow.order.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDto {

    String clientId;

    @NotBlank(message = "Garden ID cannot be null or empty")
    String gardenId;

    @NotNull(message = "Start date cannot be null")
    LocalDate startDate;

    String status;

    String workerId;

    String description;
}
