package com.vortexbird.movieticket.service;

import com.vortexbird.movieticket.model.TicketPurchase;

/**
 * Service interface for email operations.
 * 
 * Follows SOLID principles - Interface Segregation Principle.
 */
public interface IEmailService {
    
    /**
     * Sends purchase confirmation email to customer.
     * 
     * @param purchase The ticket purchase containing all purchase details
     */
    void sendPurchaseConfirmation(TicketPurchase purchase);
}
