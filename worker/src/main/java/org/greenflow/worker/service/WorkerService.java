package org.greenflow.worker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.common.model.dto.UserCreationDto;
import org.greenflow.worker.model.entity.Worker;
import org.greenflow.worker.output.persistent.WorkerRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkerService {

    private final WorkerRepository workerRepository;

    public boolean saveWorker(UserCreationDto workerDto) {
        if (workerRepository.existsByEmail(workerDto.email())) {
            log.error("Email already in use in client service: {}", workerDto.email());
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
}
