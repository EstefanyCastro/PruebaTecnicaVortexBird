package com.vortexbird.movieticket.dto;

import com.vortexbird.movieticket.model.PurchaseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for ticket purchase response.
 *
 * Contains purchase information to return to clients.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketPurchaseDTO {
    
    private Long id;
    private Long customerId;
    private String customerEmail;
    private String customerName;
    private Long movieId;
    private String movieTitle;
    private Integer quantity;
    private Double unitPrice;
    private Double totalAmount;
    private PurchaseStatus status;
    private String cardLastFour;
    private String cardHolderName;
    private LocalDateTime purchaseDate;
    private String confirmationCode;
}
