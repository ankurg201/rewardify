package com.reward.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for handling application-wide exceptions.
 * <p>
 * This class provides centralized exception handling for controllers by capturing
 * specific and generic exceptions and returning appropriate error responses.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles {@link RewardProcessingException} and returns a structured error response.
     *
     * @param ex the {@link RewardProcessingException} thrown during reward processing
     * @return a {@link ResponseEntity} containing an error message and HTTP status {@code BAD_REQUEST}
     */
    @ExceptionHandler(RewardProcessingException.class)
    public ResponseEntity<Map<String, String>> handleRewardProcessingException(RewardProcessingException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Reward Processing Error");
        errorResponse.put("message", ex.getMessage());
        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }

    /**
     * Handles generic {@link Exception} and returns a structured error response.
     * <p>
     * This method catches any unhandled exceptions and returns an internal server error response.
     * </p>
     *
     * @param ex the {@link Exception} thrown during request processing
     * @return a {@link ResponseEntity} containing an error message and HTTP status {@code INTERNAL_SERVER_ERROR}
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Internal Server Error");
        errorResponse.put("message", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
