package com.vortexbird.movieticket.service;

import com.vortexbird.movieticket.dto.MovieDTO;
import com.vortexbird.movieticket.model.Movie;

/**
 * Service interface for Movie business logic.
 *
 * Defines operations for managing movies in the system.
 */
public interface IMovieService {

    Movie createMovie(MovieDTO movieDTO);
}
