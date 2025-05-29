package org.greenflow.equipment.model.constant;

import lombok.Getter;

public enum EquipmentSortBy {

    PRICE("dailyLeasingPrice"),
    NAME("name");

    @Getter
    private final String fieldName;

    EquipmentSortBy(String fieldName) {
        this.fieldName = fieldName;
    }

    public enum SortDirection {
        ASC,
        DESC
    }
}
