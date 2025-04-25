package org.greenflow.client.service.mapper;

import org.greenflow.client.model.dto.ClientDto;
import org.greenflow.client.model.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClientMapper {

    ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

    Client toEntity(ClientDto clientDto);

    ClientDto toDto(Client client);
}
