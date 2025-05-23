package org.greenflow.worker.input.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.greenflow.common.model.constant.CustomHeaders;
import org.greenflow.common.model.dto.UserCreationDto;
import org.greenflow.worker.model.dto.WorkerDto;
import org.greenflow.worker.service.WorkerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/worker")
@RequiredArgsConstructor
public class WorkerController {

    private final WorkerService workerService;

    @PostMapping("/save")
    public boolean saveWorker(@RequestBody UserCreationDto workerDto) {
        workerService.saveWorker(workerDto);
        return true;
    }

    @GetMapping("/info")
    @PreAuthorize("hasAuthority('WORKER')")
    public ResponseEntity<WorkerDto> workerInfoById(@RequestHeader(CustomHeaders.X_USER_ID) String id) {
        WorkerDto worker = workerService.getWorkerById(id);
        if (worker == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(worker);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('WORKER')")
    public ResponseEntity<WorkerDto> updateWorker(@RequestHeader(CustomHeaders.X_USER_ID) String id,
                                                  @RequestBody @Valid WorkerDto workerDto) {
        WorkerDto worker = workerService.updateWorker(id, workerDto);
        return ResponseEntity.ok(worker);
    }

    @GetMapping("/balance")
    @PreAuthorize("hasAuthority('WORKER')")
    public ResponseEntity<?> getBalance(@RequestHeader(CustomHeaders.X_USER_ID) String userId) {
        return ResponseEntity.ok(workerService.getWorkerBalance(userId));
    }

    @PostMapping("/balance/topup")
    @PreAuthorize("hasAuthority('WORKER')")
    public ResponseEntity<Void> topUpBalance(@RequestHeader(CustomHeaders.X_USER_ID) String userId,
                                          @RequestParam("amount") @NotNull @DecimalMin("1.0") BigDecimal paymentAmount) {
        String paymentRedirectUrl = workerService.topUpBalance(userId, paymentAmount);
        URI redirectUri = URI.create(paymentRedirectUrl);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(redirectUri)
                .build();
    }
}
