package org.greenflow.order.service.mapper;

import org.greenflow.order.model.dto.OrderItemDto;
import org.greenflow.order.model.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {
    ServiceMapper.class
})
public interface OrderItemMapper {

    OrderItemMapper INSTANCE = Mappers.getMapper(OrderItemMapper.class);

    OrderItem toEntity(OrderItemDto orderItemDto);

    OrderItemDto toDto(OrderItem orderItem);
}
