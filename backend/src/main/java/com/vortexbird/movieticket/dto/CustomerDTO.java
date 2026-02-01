package com.vortexbird.movieticket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for customer information (response).
 *
 * Used to return customer data without sensitive information like password.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    
    private Long id;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private String role;
    private Boolean enabled;
    private LocalDateTime createdAt;
}
