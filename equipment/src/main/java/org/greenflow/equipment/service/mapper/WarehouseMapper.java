package org.greenflow.equipment.service.mapper;

import org.greenflow.equipment.model.dto.WarehouseCreationDto;
import org.greenflow.equipment.model.dto.WarehouseDto;
import org.greenflow.equipment.model.dto.WarehouseUpdateDto;
import org.greenflow.equipment.model.entity.Warehouse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface WarehouseMapper {

    WarehouseMapper INSTANCE = Mappers.getMapper(WarehouseMapper.class);

    WarehouseDto toDto(Warehouse warehouse);

    Warehouse toEntity(WarehouseDto warehouseDto);

    Warehouse toEntity(WarehouseCreationDto warehouseDto);

    Warehouse toEntity(WarehouseUpdateDto warehouseDto);
}
