package com.vortexbird.movieticket.controller;

import com.vortexbird.movieticket.dto.MovieDTO;
import com.vortexbird.movieticket.service.IMovieService;
import com.vortexbird.movieticket.model.Movie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Implementation of Movie REST Controller.
 *
 * Handles HTTP requests for movie management operations. Delegates business
 * logic to IMovieService.
 */
@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@Slf4j
public class MovieController {

    @Autowired
    private IMovieService movieService;

    @PostMapping
    public ResponseEntity<Movie> createMovie(@RequestBody MovieDTO movieDTO) {
        Movie createdMovie = movieService.createMovie(movieDTO);
        return ResponseEntity.ok(createdMovie);
    }
}
