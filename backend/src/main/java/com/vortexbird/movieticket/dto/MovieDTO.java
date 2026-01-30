package com.vortexbird.movieticket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for movie response.
 *
 * Contains movie data for API responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDTO {

    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private int duration;
    private String genre;
    private double price;
    private Boolean isEnabled;

    public String getTitle() {
        return title;
    }
}
