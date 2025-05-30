package org.greenflow.common.model.dto;

/**
 * Represents a request to lease equipment.
 *
 * @param equipmentId The unique identifier of the equipment.
 * @param startDate   The start date of the leasing period.
 */
public record EquipmentLeasingRequest(String equipmentId, String startDate) {

}
