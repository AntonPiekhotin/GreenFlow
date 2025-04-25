package org.greenflow.worker.service.mapper;

import org.greenflow.worker.model.dto.WorkerDto;
import org.greenflow.worker.model.entity.Worker;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface WorkerMapper {

    WorkerMapper INSTANCE = Mappers.getMapper(WorkerMapper.class);

    WorkerDto toDto(Worker worker);

    Worker toEntity(WorkerDto workerDto);
}
