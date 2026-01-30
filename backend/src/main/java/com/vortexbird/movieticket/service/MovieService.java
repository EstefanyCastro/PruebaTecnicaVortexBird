package com.vortexbird.movieticket.service;

import com.vortexbird.movieticket.dto.MovieDTO;
import com.vortexbird.movieticket.model.Movie;
import com.vortexbird.movieticket.repository.IMovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Movie movie = new Movie();
        movie.setTitle(movieDTO.getTitle());
        movie.setDescription(movieDTO.getDescription());
        movie.setImageUrl(movieDTO.getImageUrl());
        movie.setDuration(movieDTO.getDuration());
        movie.setGenre(movieDTO.getGenre());
        movie.setPrice(movieDTO.getPrice());
        movie.setIsEnabled(movieDTO.isEnabled());

        return movieRepository.save(movie);
        log.info("Movie created: {}", movie.getTitle());
    }
}
