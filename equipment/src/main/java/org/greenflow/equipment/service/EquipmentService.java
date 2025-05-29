package org.greenflow.equipment.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.common.model.exception.GreenFlowException;
import org.greenflow.equipment.model.constant.EquipmentSortBy;
import org.greenflow.equipment.model.constant.LeasingStatus;
import org.greenflow.equipment.model.entity.Equipment;
import org.greenflow.equipment.model.entity.Warehouse;
import org.greenflow.equipment.output.persistent.EquipmentRepository;
import org.greenflow.equipment.output.persistent.WarehouseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final WarehouseRepository warehouseRepository;

    public Equipment createEquipment(@Valid Equipment equipment, @NotBlank Long warehouseId) {
        if (!warehouseRepository.existsById(warehouseId)) {
            throw new GreenFlowException(400, "Warehouse with id " + warehouseId + " does not exist");
        }
        equipment.setWarehouseId(warehouseId);
        return equipmentRepository.save(equipment);
    }

    public Equipment getEquipmentById(@NotBlank String id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new GreenFlowException(400, "Equipment with id " + id + " does not exist"));
    }

    public List<Equipment> getEquipmentByWarehouseId(@NotBlank Long warehouseId) {
        if (!warehouseRepository.existsById(warehouseId)) {
            throw new GreenFlowException(400, "Warehouse with id " + warehouseId + " does not exist");
        }
        return equipmentRepository.findAllByWarehouseId(warehouseId);
    }

    public Equipment updateEquipment(String id, Equipment equipment) {
        Equipment existingEquipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new GreenFlowException(400, "Equipment with id " + id + " does not exist"));
        equipment.setId(id);
        equipment.setWarehouseId(existingEquipment.getWarehouseId());
        return equipmentRepository.save(equipment);
    }

    public void deleteEquipmentById(@NotBlank String id) {
        if (!equipmentRepository.existsById(id)) {
            throw new GreenFlowException(400, "Equipment with id " + id + " does not exist");
        }
        equipmentRepository.deleteById(id);
    }

    public List<Equipment> findAvailableNear(Double lat, Double lon, Double radiusKm,
                                             EquipmentSortBy sortBy, EquipmentSortBy.SortDirection sortDir) {
        double radiusMeters = radiusKm * 1_000;
        List<Warehouse> nearby = warehouseRepository.findWithinRadius(lat, lon, radiusMeters);
        List<Long> warehouseIds = nearby.stream()
                .map(Warehouse::getId)
                .toList();

        Sort.Direction direction = Sort.Direction.fromString(sortDir.name());
        Sort sort = Sort.by(direction, sortBy.getFieldName());

        return equipmentRepository
                .findByWarehouse_IdInAndStatus(warehouseIds, LeasingStatus.AVAILABLE, sort)
                .stream()
                .toList();
    }
}
