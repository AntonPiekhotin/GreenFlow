package org.greenflow.apigateway.exception;

import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ResponseErrorDto> handleMalformedJwtException(MalformedJwtException e) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        var error = ResponseErrorDto.builder()
                .statusCode(status)
                .errorMessage(List.of("Invalid token", e.toString()))
                .build();
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseErrorDto> handleException(Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        var error = ResponseErrorDto.builder()
                .statusCode(status)
                .errorMessage(List.of("Internal server error", e.toString()))
                .build();
        return ResponseEntity.status(status).body(error);
    }

}