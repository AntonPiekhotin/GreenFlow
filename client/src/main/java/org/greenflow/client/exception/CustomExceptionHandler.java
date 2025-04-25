package org.greenflow.client.exception;

import jakarta.validation.ConstraintViolationException;
import org.greenflow.common.model.dto.ResponseErrorDto;
import org.greenflow.common.model.exception.GreenFlowException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(GreenFlowException.class)
    public ResponseEntity<ResponseErrorDto> handleGreenFlowException(GreenFlowException e) {
        int status = e.getStatusCode();
        var error = ResponseErrorDto.builder()
                .statusCode(status)
                .errorMessage(List.of(e.getMessage()))
                .stackTrace(Arrays.stream(e.getStackTrace())
                        .map(StackTraceElement::toString)
                        .toList())
                .build();
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseErrorDto> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        int status = HttpStatus.BAD_REQUEST.value();

        // Collecting all error messages from the binding result
        List<String> errorMessages = e.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    if (error instanceof FieldError fieldError) {
                        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                    }
                    return error.getDefaultMessage();
                })
                .toList();

        var error = ResponseErrorDto.builder()
                .statusCode(status)
                .errorMessage(errorMessages)
                .stackTrace(e.getBindingResult().getAllErrors().stream()
                        .map(Object::toString).collect(Collectors.toList()))
                .build();
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseErrorDto> handleGeneralException(Exception e) {
        int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        var error = ResponseErrorDto.builder()
                .statusCode(status)
                .errorMessage(List.of(e.getMessage()))
                .stackTrace(Arrays.stream(e.getStackTrace())
                        .map(StackTraceElement::toString)
                        .toList())
                .build();
        return ResponseEntity.status(status).body(error);
    }
}
