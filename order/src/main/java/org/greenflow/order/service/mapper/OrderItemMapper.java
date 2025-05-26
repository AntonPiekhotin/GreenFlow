package org.greenflow.order.service.mapper;

import org.greenflow.order.model.dto.OrderItemDto;
import org.greenflow.order.model.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = ServiceMapper.class)
public interface OrderItemMapper {

    OrderItemMapper INSTANCE = Mappers.getMapper(OrderItemMapper.class);

    @Mapping(source = "service", target = "serviceDto")
    OrderItemDto toDto(OrderItem orderItem);

    @Mapping(source = "serviceDto", target = "service")
    OrderItem toEntity(OrderItemDto orderItemDto);
}