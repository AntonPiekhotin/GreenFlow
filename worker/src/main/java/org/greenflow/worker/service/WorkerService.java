package org.greenflow.worker.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.common.model.dto.UserCreationDto;
import org.greenflow.common.model.exception.GreenFlowException;
import org.greenflow.worker.model.dto.WorkerDto;
import org.greenflow.worker.model.entity.Worker;
import org.greenflow.worker.output.persistent.WorkerRepository;
import org.greenflow.worker.service.mapper.WorkerMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkerService {

    private final WorkerRepository workerRepository;

    public boolean saveWorker(UserCreationDto workerDto) {
        if (workerRepository.existsByEmail(workerDto.email())) {
            log.error("Email already in use in worker service: {}", workerDto.email());
            return false;
        }
        Worker worker = Worker.builder()
                .id(workerDto.id())
                .email(workerDto.email())
                .build();
        log.info("Worker {} saved", worker.getEmail());
        workerRepository.save(worker);
        return true;
    }

    public WorkerDto getWorkerById(String id) {
        return WorkerMapper.INSTANCE.toDto(workerRepository.findById(id).orElse(null));
    }

    public WorkerDto updateWorker(@NotNull String id, @NotNull @Valid WorkerDto workerDto) {
        Worker worker = workerRepository.findById(id).orElse(null);
        if (worker == null) {
            log.error("Worker not found in worker service: {}", id);
            throw new GreenFlowException(404, "Worker not found for id: " + id);
        }
        worker = WorkerMapper.INSTANCE.toEntity(workerDto);
        worker.setId(id);
        workerRepository.save(worker);

        log.info("Worker {} updated", worker.getEmail());
        return WorkerMapper.INSTANCE.toDto(worker);
    }
}
