package com.vortexbird.movieticket.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for movie response.
 *
 * Contains movie data for API responses with validation rules.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDTO {

    private Long id;
    
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;
    
    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 2000, message = "Description must be between 10 and 2000 characters")
    private String description;
    
    @Pattern(regexp = "^https?://.*", message = "Image URL must be a valid HTTP/HTTPS URL")
    private String imageUrl;
    
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 500, message = "Duration must not exceed 500 minutes")
    private int duration;
    
    @NotBlank(message = "Genre is required")
    @Size(min = 3, max = 100, message = "Genre must be between 3 and 100 characters")
    private String genre;
    
    @Positive(message = "Price must be positive")
    @DecimalMin(value = "0.01", message = "Price must be at least 0.01")
    private double price;
    
    private Boolean enabled;
}
