package com.reward.app.exception;

public class RewardProcessingException extends RuntimeException {
    public RewardProcessingException(String message) {
        super(message);
    }

    public RewardProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
