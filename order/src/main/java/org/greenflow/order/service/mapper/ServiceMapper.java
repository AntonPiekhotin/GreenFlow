package org.greenflow.order.service.mapper;

import org.greenflow.order.model.dto.ServiceDto;
import org.greenflow.order.model.entity.Service;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ServiceMapper {

    ServiceMapper INSTANCE = Mappers.getMapper(ServiceMapper.class);

    @Mapping(source = "pricePerUnit", target = "rate")
    ServiceDto toDto(Service service);

    @Mapping(source = "rate", target = "pricePerUnit")
    Service toEntity(ServiceDto serviceDto);
}