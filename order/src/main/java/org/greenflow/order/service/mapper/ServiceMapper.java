package org.greenflow.order.service.mapper;

import org.greenflow.order.model.dto.ServiceDto;
import org.greenflow.order.model.entity.Service;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ServiceMapper {

    ServiceMapper INSTANCE = Mappers.getMapper(ServiceMapper.class);

    Service toEntity(ServiceDto serviceDto);

    ServiceDto toDto(Service service);
}
