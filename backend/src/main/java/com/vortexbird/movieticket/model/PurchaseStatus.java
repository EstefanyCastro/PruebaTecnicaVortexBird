package com.vortexbird.movieticket.model;

/**
 * Enum representing the possible states of a ticket purchase.
 */
public enum PurchaseStatus {
    /**
     * Purchase is pending payment confirmation.
     */
    PENDING,
    
    /**
     * Purchase has been confirmed and tickets are valid.
     */
    CONFIRMED,
    
    /**
     * Purchase was cancelled by the customer or system.
     */
    CANCELLED,
    
    /**
     * Purchase has been refunded.
     */
    REFUNDED
}
