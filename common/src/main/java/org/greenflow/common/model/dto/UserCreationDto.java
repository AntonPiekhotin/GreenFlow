package org.greenflow.common.model.dto;

import lombok.Builder;

/**
 * Represents a request to create a user.
 *
 * @param id    The unique identifier of the user.
 * @param email The email address of the user.
 */
@Builder
public record UserCreationDto(String id, String email) {
}
