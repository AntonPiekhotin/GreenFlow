package org.greenflow.order.model.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {

    CREATED("Created"),
    OPEN("Open"),
    ASSIGNED("Assigned"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String status;

}
