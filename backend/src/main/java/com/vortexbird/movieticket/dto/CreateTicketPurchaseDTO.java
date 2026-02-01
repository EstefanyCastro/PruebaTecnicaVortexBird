package com.vortexbird.movieticket.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a ticket purchase.
 *
 * Contains information needed to create a new purchase transaction.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTicketPurchaseDTO {
    
    @NotNull(message = "Movie ID is required")
    private Long movieId;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 10, message = "Quantity must not exceed 10")
    private Integer quantity;
    
    @Valid
    @NotNull(message = "Payment information is required")
    private PaymentInfoDTO paymentInfo;
}
