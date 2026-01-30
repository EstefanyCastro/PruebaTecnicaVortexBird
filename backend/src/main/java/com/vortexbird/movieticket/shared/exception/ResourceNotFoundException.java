package com.vortexbird.movieticket.shared.exception;

/**
 * Custom exception for resource not found scenarios.
 * 
 * Thrown when a requested resource cannot be found in the database.
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
