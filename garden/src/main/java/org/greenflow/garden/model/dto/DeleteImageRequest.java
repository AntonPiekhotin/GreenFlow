package org.greenflow.garden.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DeleteImageRequest(
        @NotNull Long gardenId,
        @NotBlank String imageUrl
) {
}
