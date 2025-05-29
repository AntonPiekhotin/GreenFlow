package org.greenflow.equipment.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.common.model.exception.GreenFlowException;
import org.greenflow.equipment.model.dto.WarehouseCreationDto;
import org.greenflow.equipment.model.dto.WarehouseDto;
import org.greenflow.equipment.model.dto.WarehouseUpdateDto;
import org.greenflow.equipment.model.entity.Warehouse;
import org.greenflow.equipment.output.persistent.WarehouseRepository;
import org.greenflow.equipment.service.mapper.WarehouseMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    public WarehouseDto createWarehouse(@NotNull @Valid WarehouseCreationDto warehouseCreationDto) {
        Warehouse warehouse = WarehouseMapper.INSTANCE.toEntity(warehouseCreationDto);
        warehouse = warehouseRepository.save(warehouse);
        return WarehouseMapper.INSTANCE.toDto(warehouse);
    }

    public WarehouseDto getWarehouse(@NotBlank Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new GreenFlowException(400, "Warehouse with id " + id + " does not exist"));
        return WarehouseMapper.INSTANCE.toDto(warehouse);
    }

    public WarehouseDto updateWarehouse(@NotBlank Long id, @NotNull @Valid WarehouseUpdateDto warehouseUpdateDto) {
        if (!warehouseRepository.existsById(id)) {
            throw new GreenFlowException(400, "Warehouse with id " + id + " does not exist");
        }
        Warehouse updatedWarehouse = WarehouseMapper.INSTANCE.toEntity(warehouseUpdateDto);
        updatedWarehouse.setId(id);
        return WarehouseMapper.INSTANCE.toDto(warehouseRepository.save(updatedWarehouse));
    }

    public void deleteWarehouseById(@NotBlank Long id) {
        if (!warehouseRepository.existsById(id)) {
            throw new GreenFlowException(400, "Warehouse with id " + id + " does not exist");
        }
        warehouseRepository.deleteById(id);
    }

}
