package org.greenflow.common.model.dto;

import lombok.Builder;

@Builder
public record UserCreationDto(String id, String email) {
}
