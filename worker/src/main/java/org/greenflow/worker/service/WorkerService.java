package org.greenflow.worker.service;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.common.model.dto.UserCreationDto;
import org.greenflow.common.model.exception.GreenFlowException;
import org.greenflow.worker.model.dto.WorkerDto;
import org.greenflow.worker.model.entity.Worker;
import org.greenflow.worker.output.persistent.WorkerRepository;
import org.greenflow.worker.service.mapper.WorkerMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkerService {

    private final WorkerRepository workerRepository;
    private final RestTemplate restTemplate;

    @Value("${host.billing}")
    private String BILLING_SERVICE_HOST;

    private String BILLING_SERVICE_URL;

    @PostConstruct
    public void init() {
        BILLING_SERVICE_URL = "http://" + BILLING_SERVICE_HOST + "/api/v1/billing";
    }

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

    public BigDecimal getWorkerBalance(String userId) {
        try {
            BigDecimal response = restTemplate.getForObject(BILLING_SERVICE_URL + "/balance?userId=" + userId,
                    BigDecimal.class);
            return response;
        } catch (Exception e) {
            log.error("Error occurred while getting worker balance", e);
            throw new GreenFlowException(503, "Error occurred while getting worker balance");
        }
    }

    public String topUpBalance(String userId, @NotNull @DecimalMin("1.0") BigDecimal paymentAmount) {
        try {
            String response = restTemplate.postForObject(BILLING_SERVICE_URL + "/topup?userId=" + userId +
                    "&amount=" + paymentAmount, null, String.class);
            return response;
        } catch (Exception e) {
            log.error("Error occurred while topping up worker balance", e);
            throw new GreenFlowException(503, "Error occurred while topping up worker balance");
        }
    }
}
