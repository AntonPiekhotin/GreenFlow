package org.greenflow.client.model.dto;

import lombok.Builder;

@Builder
public record ClientCreationDto(String id, String email) {
}
