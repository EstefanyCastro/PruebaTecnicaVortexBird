package com.vortexbird.movieticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main application class for Movie Ticket Booking API.
 *
 * Entry point for the Spring Boot application. Initializes and runs the Movie
 * Ticket Booking System backend.
 *
 * @EnableAsync enables asynchronous method execution for email sending.
 */
@SpringBootApplication
@EnableAsync
public class MovieTicketApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieTicketApplication.class, args);
    }
}
