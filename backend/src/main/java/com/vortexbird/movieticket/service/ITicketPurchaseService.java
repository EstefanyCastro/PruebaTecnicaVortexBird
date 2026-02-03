package com.vortexbird.movieticket.service;

import com.vortexbird.movieticket.dto.CreateTicketPurchaseDTO;
import com.vortexbird.movieticket.dto.TicketPurchaseDTO;
import com.vortexbird.movieticket.model.TicketPurchase;

import java.util.List;

/**
 * Service interface for TicketPurchase business logic.
 *
 * Defines operations for managing ticket purchases.
 */
public interface ITicketPurchaseService {

    TicketPurchase createPurchase(Long customerId, CreateTicketPurchaseDTO dto);

    TicketPurchase getPurchaseById(Long id);

    List<TicketPurchaseDTO> getCustomerPurchases(Long customerId);

    List<TicketPurchaseDTO> getMoviePurchases(Long movieId);

    void cancelPurchase(Long id);

    TicketPurchaseDTO toDTO(TicketPurchase purchase);
}
