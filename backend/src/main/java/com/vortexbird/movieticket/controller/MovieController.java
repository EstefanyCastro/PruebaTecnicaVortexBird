package com.vortexbird.movieticket.controller;

import com.vortexbird.movieticket.dto.MovieDTO;
import com.vortexbird.movieticket.service.IMovieService;
import com.vortexbird.movieticket.service.IStorageService;
import com.vortexbird.movieticket.model.Movie;
import com.vortexbird.movieticket.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    private final IMovieService movieService;
    private final IStorageService storageService;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<Movie>> createMovie(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("genre") String genre,
            @RequestParam("duration") int duration,
            @RequestParam("price") double price,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        
        log.info("POST /movies - Creating movie: {}", title);
        
        try {
            String imageUrl = null;
            if (image != null && !image.isEmpty()) {
                imageUrl = storageService.uploadFile(image);
                log.info("Image uploaded to S3: {}", imageUrl);
            }
            
            MovieDTO movieDTO = new MovieDTO();
            movieDTO.setTitle(title);
            movieDTO.setDescription(description);
            movieDTO.setGenre(genre);
            movieDTO.setDuration(duration);
            movieDTO.setPrice(price);
            movieDTO.setImageUrl(imageUrl);
            
            Movie createdMovie = movieService.createMovie(movieDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(createdMovie, "Movie created successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Invalid data: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error creating movie: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create movie"));
        }
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

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<Movie>> updateMovie(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("genre") String genre,
            @RequestParam("duration") int duration,
            @RequestParam("price") double price,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "imageUrl", required = false) String existingImageUrl) {
        
        log.info("PUT /movies/{} - Updating movie", id);
        
        try {
            String imageUrl = existingImageUrl;
            
            // Si se envió una nueva imagen, subirla a S3
            if (image != null && !image.isEmpty()) {
                imageUrl = storageService.uploadFile(image);
                log.info("New image uploaded to S3: {}", imageUrl);
            }
            
            MovieDTO movieDTO = new MovieDTO();
            movieDTO.setTitle(title);
            movieDTO.setDescription(description);
            movieDTO.setGenre(genre);
            movieDTO.setDuration(duration);
            movieDTO.setPrice(price);
            movieDTO.setImageUrl(imageUrl);
            
            Movie updatedMovie = movieService.updateMovie(id, movieDTO);
            return ResponseEntity.ok(ApiResponse.success(updatedMovie, "Movie updated successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Invalid data: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating movie: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update movie"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> disableMovie(@PathVariable Long id) {
        log.info("DELETE /movies/{} - Disabling movie", id);
        movieService.disableMovie(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Movie disabled successfully"));
    }
}
