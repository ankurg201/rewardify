package com.reward.app.exception;

import org.springframework.http.HttpStatus;

/**
 * Custom exception class for handling errors related to reward processing.
 * <p>
 * This exception is thrown when an issue occurs during the calculation or processing of reward points,
 * such as invalid transaction data or negative amounts.
 * </p>
 */
public class RewardProcessingException extends RuntimeException {

    private final HttpStatus status; // Store status dynamically

    public RewardProcessingException(String message, HttpStatus status) {
        super(message == null || message.trim().isEmpty() ? "Reward processing error occurred" : message);
        this.status = status != null ? status : HttpStatus.BAD_REQUEST; // Default to 400 if null
    }

    public RewardProcessingException(String message, Throwable cause, HttpStatus status) {
        super(message == null || message.trim().isEmpty() ? "Reward processing error occurred" : message, cause);
        this.status = status != null ? status : HttpStatus.BAD_REQUEST; // Default to 400 if null
    }

    public HttpStatus getStatus() {
        return status;
    }
}
