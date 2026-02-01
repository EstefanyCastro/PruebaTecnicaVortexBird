package com.vortexbird.movieticket.controller;

import com.vortexbird.movieticket.dto.MovieDTO;
import com.vortexbird.movieticket.service.IMovieService;
import com.vortexbird.movieticket.model.Movie;
import com.vortexbird.movieticket.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Implementation of Movie REST Controller.
 *
 * Handles HTTP requests for movie management operations. Delegates business
 * logic to IMovieService.
 */
@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
@Slf4j
public class MovieController {

    @Autowired
    private IMovieService movieService;

    @PostMapping
    public ResponseEntity<ApiResponse<Movie>> createMovie(@Valid @RequestBody MovieDTO movieDTO) {
        log.info("POST /movies - Creating movie: {}", movieDTO.getTitle());
        Movie createdMovie = movieService.createMovie(movieDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdMovie, "Movie created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Movie>>> getAllMovies(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String genre) {
        log.info("GET /movies - Searching movies with title: '{}' and genre: '{}'", title, genre);
        List<Movie> movies = movieService.searchMovies(title, genre);
        return ResponseEntity.ok(ApiResponse.success(movies, "Movies retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Movie>> getMovieById(@PathVariable Long id) {
        log.info("GET /movies/{} - Fetching movie", id);
        Movie movie = movieService.getMovieById(id);
        return ResponseEntity.ok(ApiResponse.success(movie, "Movie retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Movie>> updateMovie(
            @PathVariable Long id,
            @Valid @RequestBody MovieDTO movieDTO) {
        log.info("PUT /movies/{} - Updating movie", id);
        Movie updatedMovie = movieService.updateMovie(id, movieDTO);
        return ResponseEntity.ok(ApiResponse.success(updatedMovie, "Movie updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> disableMovie(@PathVariable Long id) {
        log.info("DELETE /movies/{} - Disabling movie", id);
        movieService.disableMovie(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Movie disabled successfully"));
    }
}
