package com.vortexbird.movieticket.service;

import com.vortexbird.movieticket.dto.MovieDTO;
import com.vortexbird.movieticket.model.Movie;

import java.util.List;

/**
 * Service interface for Movie business logic.
 *
 * Defines operations for managing movies in the system.
 */
public interface IMovieService {

    Movie createMovie(MovieDTO movieDTO);

    List<Movie> getAllMovies();

    Movie getMovieById(Long id);

    Movie updateMovie(Long id, MovieDTO movieDTO);

    void disableMovie(Long id);

    List<Movie> searchMovies(String title, String genre);
}
