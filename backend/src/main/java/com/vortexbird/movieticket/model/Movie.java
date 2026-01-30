package com.vortexbird.movieticket.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Movie entity representing a movie in the system.
 *
 * Contains information about movies available for ticket purchase including
 * title, description, image reference and pricing.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false, length = 1000)
    private String imageUrl;

    @Column(name = "duration_minutes", nullable = false)
    private int duration;

    @Column(name = "genre", length = 100, nullable = false)
    private String genre;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "is_enabled")
    private Boolean isEnabled;

}
