package com.vortexbird.movieticket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for payment information.
 *
 * Contains credit card details for payment processing.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInfoDTO {
    
    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "^[0-9]{16}$", message = "Card number must be 16 digits")
    private String cardNumber;
    
    @NotBlank(message = "Card holder name is required")
    @Size(min = 3, max = 200, message = "Card holder name must be between 3 and 200 characters")
    private String cardHolderName;
    
    @NotBlank(message = "Expiry date is required")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/[0-9]{2}$", message = "Expiry date must be in MM/YY format")
    private String expiryDate;
    
    @NotBlank(message = "CVV is required")
    @Pattern(regexp = "^[0-9]{3,4}$", message = "CVV must be 3 or 4 digits")
    private String cvv;
}
