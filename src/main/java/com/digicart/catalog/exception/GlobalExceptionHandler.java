package com.digicart.catalog.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Maps domain and validation failures to HTTP error payloads.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Handle validation.
     *
     * @param ex ex
     * @return HTTP response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                e -> e.getField(),
                e -> e.getDefaultMessage(),
                (a, b) -> a
            ));
        return ResponseEntity.badRequest().body(Map.of("error", errors));
    }

    /**
     * Handle bad arg.
     *
     * @param ex ex
     * @return HTTP response
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleBadArg(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    /**
     * Handle generic.
     *
     * @param ex ex
     * @return HTTP response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error"));
    }
}
