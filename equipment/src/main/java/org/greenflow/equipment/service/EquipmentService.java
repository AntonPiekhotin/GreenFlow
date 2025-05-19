package org.greenflow.equipment.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.common.model.exception.GreenFlowException;
import org.greenflow.equipment.model.entity.Equipment;
import org.greenflow.equipment.ouput.persistent.EquipmentRepository;
import org.greenflow.equipment.ouput.persistent.WarehouseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final WarehouseRepository warehouseRepository;

    public Equipment createEquipment(@Valid Equipment equipment, @NotBlank String warehouseId) {
        equipment.setWarehouse(warehouseRepository.findById(warehouseId).orElseThrow( () ->
                new GreenFlowException(400, "Warehouse with id " + warehouseId + " does not exist")));
        return equipmentRepository.save(equipment);
    }

    public Equipment getEquipmentById(@NotBlank String id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new GreenFlowException(400, "Equipment with id " + id + " does not exist"));
    }

    public List<Equipment> getEquipmentByWarehouseId(@NotBlank String warehouseId) {
        if (!warehouseRepository.existsById(warehouseId)) {
            throw new GreenFlowException(400, "Warehouse with id " + warehouseId + " does not exist");
        }
        return equipmentRepository.findAllByWarehouseId(warehouseId);
    }

    public Equipment updateEquipment(String id, Equipment equipment) {
        Equipment existingEquipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new GreenFlowException(400, "Equipment with id " + id + " does not exist"));
        equipment.setId(id);
        equipment.setWarehouse(warehouseRepository.findById(existingEquipment.getWarehouse().getId())
                .orElseThrow( () ->  new GreenFlowException(400,
                        "Warehouse with id " + existingEquipment.getWarehouse().getId() + " does not exist"))
        );
        return equipmentRepository.save(equipment);
    }

    public void deleteEquipmentById(@NotBlank String id) {
        if (!equipmentRepository.existsById(id)) {
            throw new GreenFlowException(400, "Equipment with id " + id + " does not exist");
        }
        equipmentRepository.deleteById(id);
    }

}
