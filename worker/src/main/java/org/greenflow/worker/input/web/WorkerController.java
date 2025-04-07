package org.greenflow.worker.input.web;

import lombok.RequiredArgsConstructor;
import org.greenflow.worker.model.dto.WorkerCreationDto;
import org.greenflow.worker.service.WorkerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/worker")
@RequiredArgsConstructor
public class WorkerController {

    private final WorkerService workerService;

    @PostMapping("/save")
    public boolean saveWorker(@RequestBody WorkerCreationDto workerCreationDto) {
        workerService.saveWorker(workerCreationDto);
        return true;
    }
}
