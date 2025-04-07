package org.greenflow.worker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.worker.model.dto.WorkerCreationDto;
import org.greenflow.worker.model.entity.Worker;
import org.greenflow.worker.output.persistent.WorkerRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkerService {

    private final WorkerRepository workerRepository;

    public boolean saveWorker(WorkerCreationDto workerCreationDto) {
        if (workerRepository.existsByEmail(workerCreationDto.email())) {
            log.error("Email already in use in client service: {}", workerCreationDto.email());
            return false;
        }
        Worker worker = Worker.builder()
                .id(workerCreationDto.id())
                .email(workerCreationDto.email())
                .build();
        log.info("Worker {} saved", worker.getEmail());
        workerRepository.save(worker);
        return true;
    }
}
