package com.reward.app.exception;

/**
 * Custom exception class for handling errors related to reward processing.
 * <p>
 * This exception is thrown when an issue occurs during the calculation or processing of reward points,
 * such as invalid transaction data or negative amounts.
 * </p>
 */
public class RewardProcessingException extends RuntimeException {
    /**
     * Constructs a new {@code RewardProcessingException} with the specified detail message.
     *
     * @param message the detail message describing the reason for the exception
     */
    public RewardProcessingException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code RewardProcessingException} with the specified detail message and cause.
     *
     * @param message the detail message describing the reason for the exception
     * @param cause   the underlying cause of the exception
     */
    public RewardProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
