package org.greenflow.common.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents an error response with details about the error.
 *
 * <p>{@code time}         - The timestamp of the error occurrence.
 * <p>{@code statusCode}   - The HTTP status code of the error.
 * <p>{@code errorMessage} - A list of error messages.
 * <p>{@code stackTrace}   - A list of stack trace elements.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseErrorDto {

    @Builder.Default
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime time = LocalDateTime.now();

    int statusCode;

    List<String> errorMessage;

    List<String> stackTrace;

}