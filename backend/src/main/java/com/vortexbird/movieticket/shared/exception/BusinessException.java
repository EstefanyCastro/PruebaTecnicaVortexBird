package com.vortexbird.movieticket.shared.exception;

/**
 * Custom exception for invalid business logic scenarios.
 * 
 * Thrown when a business rule is violated or invalid operation is attempted.
 */
public class BusinessException extends RuntimeException {
    
    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
