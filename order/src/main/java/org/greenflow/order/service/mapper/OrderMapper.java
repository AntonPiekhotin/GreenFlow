package org.greenflow.order.service.mapper;

import org.greenflow.order.model.dto.OrderCreationDto;
import org.greenflow.order.model.dto.OrderDto;
import org.greenflow.order.model.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {
    OrderItemMapper.class,
    ServiceMapper.class
})
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    OrderDto toDto(Order order);

    Order toEntity(OrderDto orderDto);

    Order toEntity(OrderCreationDto orderCreationDto);

}
