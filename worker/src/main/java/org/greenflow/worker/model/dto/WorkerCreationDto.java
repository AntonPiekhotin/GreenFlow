package org.greenflow.worker.model.dto;

import lombok.Builder;

@Builder
public record WorkerCreationDto(String id, String email) {
}
