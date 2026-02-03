package com.vortexbird.movieticket.service;

import com.vortexbird.movieticket.dto.MovieDTO;
import com.vortexbird.movieticket.model.Movie;
import com.vortexbird.movieticket.repository.IMovieRepository;
import com.vortexbird.movieticket.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MovieService.
 * 
 * Tests follow the AAA pattern:
 * - Arrange: Setup test data and mocks
 * - Act: Execute the method under test
 * - Assert: Verify the results
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MovieService Tests")
class MovieServiceTest {

    @Mock
    private IMovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    private MovieDTO validMovieDTO;
    private Movie movie;

    @BeforeEach
    void setUp() {
        // Arrange: Setup common test data
        validMovieDTO = new MovieDTO();
        validMovieDTO.setTitle("Test Movie");
        validMovieDTO.setDescription("Test Description for movie");
        validMovieDTO.setImageUrl("https://example.com/image.jpg");
        validMovieDTO.setDuration(120);
        validMovieDTO.setGenre("Action");
        validMovieDTO.setPrice(15000.0);
        validMovieDTO.setEnabled(true);

        movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Test Movie");
        movie.setDescription("Test Description for movie");
        movie.setImageUrl("https://example.com/image.jpg");
        movie.setDuration(120);
        movie.setGenre("Action");
        movie.setPrice(15000.0);
        movie.setIsEnabled(true);
    }

    @Test
    @DisplayName("Should create movie successfully")
    void testCreateMovie_Success() {
        // Arrange
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);

        // Act
        Movie result = movieService.createMovie(validMovieDTO);

        // Assert
        assertNotNull(result);
        assertEquals(movie.getTitle(), result.getTitle());
        assertEquals(movie.getDescription(), result.getDescription());
        assertEquals(movie.getPrice(), result.getPrice());
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    @DisplayName("Should get all enabled movies")
    void testGetAllMovies_Success() {
        // Arrange
        Movie movie2 = new Movie();
        movie2.setId(2L);
        movie2.setTitle("Movie 2");
        movie2.setDescription("Description 2 for movie");
        movie2.setIsEnabled(true);

        List<Movie> movies = Arrays.asList(movie, movie2);
        when(movieRepository.findAllEnabled()).thenReturn(movies);

        // Act
        List<Movie> result = movieService.getAllMovies();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(movieRepository, times(1)).findAllEnabled();
    }

    @Test
    @DisplayName("Should get movie by ID successfully")
    void testGetMovieById_Success() {
        // Arrange
        when(movieRepository.findByIdAndIsEnabledTrue(1L)).thenReturn(Optional.of(movie));

        // Act
        Movie result = movieService.getMovieById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Movie", result.getTitle());
        verify(movieRepository, times(1)).findByIdAndIsEnabledTrue(1L);
    }

    @Test
    @DisplayName("Should throw exception when movie not found")
    void testGetMovieById_NotFound() {
        // Arrange
        when(movieRepository.findByIdAndIsEnabledTrue(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            movieService.getMovieById(999L);
        });
        verify(movieRepository, times(1)).findByIdAndIsEnabledTrue(999L);
    }

    @Test
    @DisplayName("Should update movie successfully")
    void testUpdateMovie_Success() {
        // Arrange
        when(movieRepository.findByIdAndIsEnabledTrue(1L)).thenReturn(Optional.of(movie));
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);

        MovieDTO updateDTO = new MovieDTO();
        updateDTO.setTitle("Updated Title");
        updateDTO.setDescription("Updated Description for movie");
        updateDTO.setImageUrl("https://example.com/new-image.jpg");
        updateDTO.setDuration(150);
        updateDTO.setGenre("Drama");
        updateDTO.setPrice(18000.0);

        // Act
        Movie result = movieService.updateMovie(1L, updateDTO);

        // Assert
        assertNotNull(result);
        verify(movieRepository, times(1)).findByIdAndIsEnabledTrue(1L);
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    @DisplayName("Should disable movie successfully")
    void testDisableMovie_Success() {
        // Arrange
        when(movieRepository.findByIdAndIsEnabledTrue(1L)).thenReturn(Optional.of(movie));
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);

        // Act
        movieService.disableMovie(1L);

        // Assert
        verify(movieRepository, times(1)).findByIdAndIsEnabledTrue(1L);
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    @DisplayName("Should search movies by title")
    void testSearchMovies_ByTitle() {
        // Arrange
        String searchTitle = "Test";
        when(movieRepository.findByTitle(searchTitle)).thenReturn(Arrays.asList(movie));

        // Act
        List<Movie> result = movieService.searchMovies(searchTitle, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(movieRepository, times(1)).findByTitle(searchTitle);
        verify(movieRepository, never()).findByGenre(anyString());
    }

    @Test
    @DisplayName("Should search movies by genre")
    void testSearchMovies_ByGenre() {
        // Arrange
        String searchGenre = "Action";
        when(movieRepository.findByGenre(searchGenre)).thenReturn(Arrays.asList(movie));

        // Act
        List<Movie> result = movieService.searchMovies(null, searchGenre);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(movieRepository, times(1)).findByGenre(searchGenre);
        verify(movieRepository, never()).findByTitle(anyString());
    }

    @Test
    @DisplayName("Should return all movies when no search criteria")
    void testSearchMovies_NoFilter() {
        // Arrange
        when(movieRepository.findAllEnabled()).thenReturn(Arrays.asList(movie));

        // Act
        List<Movie> result = movieService.searchMovies(null, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(movieRepository, times(1)).findAllEnabled();
        verify(movieRepository, never()).findByTitle(anyString());
        verify(movieRepository, never()).findByGenre(anyString());
    }

    @Test
    @DisplayName("Should search by title and genre when both provided")
    void testSearchMovies_TitlePriorityOverGenre() {
        // Arrange
        String searchTitle = "Test";
        String searchGenre = "Action";
        when(movieRepository.findByTitleAndGenre(searchTitle, searchGenre)).thenReturn(Arrays.asList(movie));

        // Act
        List<Movie> result = movieService.searchMovies(searchTitle, searchGenre);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(movieRepository, times(1)).findByTitleAndGenre(searchTitle, searchGenre);
        verify(movieRepository, never()).findByTitle(anyString());
        verify(movieRepository, never()).findByGenre(anyString());
    }
}
