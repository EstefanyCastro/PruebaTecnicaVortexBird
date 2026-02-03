package com.vortexbird.movieticket.service;

import com.vortexbird.movieticket.dto.MovieDTO;
import com.vortexbird.movieticket.model.Movie;
import com.vortexbird.movieticket.repository.IMovieRepository;
import com.vortexbird.movieticket.shared.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of Movie Service.
 *
 * Contains business logic for movie management operations. Implements the
 * Service pattern for clean architecture.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MovieService implements IMovieService {

    @Autowired
    private IMovieRepository movieRepository;
     

    @Override
    public Movie createMovie(MovieDTO movieDTO) {
        log.info("Creating movie: {}", movieDTO.getTitle());
        Movie movie = new Movie();
        movie.setTitle(movieDTO.getTitle());
        movie.setDescription(movieDTO.getDescription());
        movie.setImageUrl(movieDTO.getImageUrl());
        movie.setDuration(movieDTO.getDuration());
        movie.setGenre(movieDTO.getGenre());
        movie.setPrice(movieDTO.getPrice());
        movie.setIsEnabled(movieDTO.getEnabled());

        Movie savedMovie = movieRepository.save(movie);
        log.info("Movie created successfully: {}", savedMovie.getTitle());
        return savedMovie;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movie> getAllMovies() {
        log.info("Fetching all enabled movies");
        return movieRepository.findAllEnabled();
    }

    @Override
    @Transactional(readOnly = true)
    public Movie getMovieById(Long id) {
        log.info("Fetching movie with id: {}", id);
        return movieRepository.findByIdAndIsEnabledTrue(id)
            .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
    }

    @Override
    public Movie updateMovie(Long id, MovieDTO movieDTO) {
        log.info("Updating movie with id: {}", id);
        Movie movie = getMovieById(id);
        
        movie.setTitle(movieDTO.getTitle());
        movie.setDescription(movieDTO.getDescription());
        movie.setImageUrl(movieDTO.getImageUrl());
        movie.setDuration(movieDTO.getDuration());
        movie.setGenre(movieDTO.getGenre());
        movie.setPrice(movieDTO.getPrice());
        
        Movie updatedMovie = movieRepository.save(movie);
        log.info("Movie updated successfully: {}", updatedMovie.getTitle());
        return updatedMovie;
    }

    @Override
    public void disableMovie(Long id) {
        log.info("Disabling movie with id: {}", id);
        Movie movie = getMovieById(id);
        movie.setIsEnabled(false);
        movieRepository.save(movie);
        log.info("Movie disabled successfully: {}", movie.getTitle());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movie> searchMovies(String title, String genre) {
        log.info("Searching movies with title: '{}' and genre: '{}'", title, genre);
        
        boolean hasTitle = title != null && !title.trim().isEmpty();
        boolean hasGenre = genre != null && !genre.trim().isEmpty();
        
        // Both title and genre
        if (hasTitle && hasGenre) {
            return movieRepository.findByTitleAndGenre(title, genre);
        }
        
        // Only title
        if (hasTitle) {
            return movieRepository.findByTitle(title);
        }
        
        // Only genre
        if (hasGenre) {
            return movieRepository.findByGenre(genre);
        }
        
        // No filters
        return getAllMovies();
    }
}
