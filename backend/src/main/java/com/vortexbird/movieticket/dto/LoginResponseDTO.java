package com.vortexbird.movieticket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for login response.
 *
 * Contains JWT token and basic customer information after successful authentication.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    
    private String token;
    private Long customerId;
    private String email;
    private String firstName;
    private String lastName;
}
