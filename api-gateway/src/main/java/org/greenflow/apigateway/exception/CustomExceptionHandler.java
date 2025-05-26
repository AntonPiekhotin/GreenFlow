package org.greenflow.apigateway.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.greenflow.common.model.dto.ResponseErrorDto;
import org.greenflow.common.model.exception.GreenFlowException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.List;

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

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ResponseErrorDto> handleMalformedJwtException(MalformedJwtException e) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        var error = ResponseErrorDto.builder()
                .statusCode(status.value())
                .errorMessage(List.of("Invalid token: " + e.toString()))
                .build();
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ResponseErrorDto> handleExpiredJwtException(ExpiredJwtException e) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        var error = ResponseErrorDto.builder()
                .statusCode(status.value())
                .errorMessage(List.of("Token expired: " + e.toString()))
                .build();
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseErrorDto> handleException(Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        var error = ResponseErrorDto.builder()
                .statusCode(status.value())
                .errorMessage(List.of("Internal server error: " + e.getMessage()))
                .stackTrace(Arrays.stream(e.getStackTrace())
                        .map(StackTraceElement::toString)
                        .toList())
                .build();
        return ResponseEntity.status(status).body(error);
    }

}