package com.vortexbird.movieticket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vortexbird.movieticket.dto.MovieDTO;
import com.vortexbird.movieticket.model.Movie;
import com.vortexbird.movieticket.service.IMovieService;
import com.vortexbird.movieticket.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for MovieController.
 * 
 * Tests follow the AAA pattern:
 * - Arrange: Setup test data and mock service responses
 * - Act: Perform HTTP requests
 * - Assert: Verify response status and content
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Disable security for tests
@ActiveProfiles("test") // Use test configuration with H2 database
@DisplayName("MovieController Tests")
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IMovieService movieService;

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
    @DisplayName("POST /movies - Should create movie successfully")
    void testCreateMovie_Success() throws Exception {
        // Arrange
        when(movieService.createMovie(any(MovieDTO.class))).thenReturn(movie);

        // Act & Assert
        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validMovieDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Movie created successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Test Movie"));

        verify(movieService, times(1)).createMovie(any(MovieDTO.class));
    }

    @Test
    @DisplayName("POST /movies - Should return 400 for invalid data")
    void testCreateMovie_InvalidData() throws Exception {
        // Arrange
        MovieDTO invalidDTO = new MovieDTO();
        invalidDTO.setTitle(""); // Empty title (invalid)
        invalidDTO.setDescription("Short"); // Too short description
        invalidDTO.setDuration(-1); // Invalid duration

        // Act & Assert
        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(movieService, never()).createMovie(any(MovieDTO.class));
    }

    @Test
    @DisplayName("GET /movies - Should return all movies")
    void testGetAllMovies_Success() throws Exception {
        // Arrange
        Movie movie2 = new Movie();
        movie2.setId(2L);
        movie2.setTitle("Movie 2");
        movie2.setIsEnabled(true);

        List<Movie> movies = Arrays.asList(movie, movie2);
        when(movieService.searchMovies(null, null)).thenReturn(movies);

        // Act & Assert
        mockMvc.perform(get("/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].title").value("Test Movie"))
                .andExpect(jsonPath("$.data[1].title").value("Movie 2"));

        verify(movieService, times(1)).searchMovies(null, null);
    }

    @Test
    @DisplayName("GET /movies?title=Test - Should search by title")
    void testGetAllMovies_SearchByTitle() throws Exception {
        // Arrange
        when(movieService.searchMovies("Test", null)).thenReturn(Arrays.asList(movie));

        // Act & Assert
        mockMvc.perform(get("/movies")
                .param("title", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].title").value("Test Movie"));

        verify(movieService, times(1)).searchMovies("Test", null);
    }

    @Test
    @DisplayName("GET /movies?genre=Action - Should search by genre")
    void testGetAllMovies_SearchByGenre() throws Exception {
        // Arrange
        when(movieService.searchMovies(null, "Action")).thenReturn(Arrays.asList(movie));

        // Act & Assert
        mockMvc.perform(get("/movies")
                .param("genre", "Action"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].genre").value("Action"));

        verify(movieService, times(1)).searchMovies(null, "Action");
    }

    @Test
    @DisplayName("GET /movies/{id} - Should return movie by ID")
    void testGetMovieById_Success() throws Exception {
        // Arrange
        when(movieService.getMovieById(1L)).thenReturn(movie);

        // Act & Assert
        mockMvc.perform(get("/movies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Test Movie"));

        verify(movieService, times(1)).getMovieById(1L);
    }

    @Test
    @DisplayName("GET /movies/{id} - Should return 404 when movie not found")
    void testGetMovieById_NotFound() throws Exception {
        // Arrange
        when(movieService.getMovieById(anyLong()))
                .thenThrow(new ResourceNotFoundException("Movie not found with id: 999"));

        // Act & Assert
        mockMvc.perform(get("/movies/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Movie not found with id: 999"));

        verify(movieService, times(1)).getMovieById(999L);
    }

    @Test
    @DisplayName("PUT /movies/{id} - Should update movie successfully")
    void testUpdateMovie_Success() throws Exception {
        // Arrange
        MovieDTO updateDTO = new MovieDTO();
        updateDTO.setTitle("Updated Title");
        updateDTO.setDescription("Updated Description for movie");
        updateDTO.setImageUrl("https://example.com/new-image.jpg");
        updateDTO.setDuration(150);
        updateDTO.setGenre("Drama");
        updateDTO.setPrice(18000.0);

        Movie updatedMovie = new Movie();
        updatedMovie.setId(1L);
        updatedMovie.setTitle("Updated Title");
        updatedMovie.setDescription("Updated Description for movie");

        when(movieService.updateMovie(eq(1L), any(MovieDTO.class))).thenReturn(updatedMovie);

        // Act & Assert
        mockMvc.perform(put("/movies/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Movie updated successfully"))
                .andExpect(jsonPath("$.data.title").value("Updated Title"));

        verify(movieService, times(1)).updateMovie(eq(1L), any(MovieDTO.class));
    }

    @Test
    @DisplayName("DELETE /movies/{id} - Should disable movie successfully")
    void testDisableMovie_Success() throws Exception {
        // Arrange
        doNothing().when(movieService).disableMovie(1L);

        // Act & Assert
        mockMvc.perform(delete("/movies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Movie disabled successfully"));

        verify(movieService, times(1)).disableMovie(1L);
    }

    @Test
    @DisplayName("DELETE /movies/{id} - Should return 404 when movie not found")
    void testDisableMovie_NotFound() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Movie not found with id: 999"))
                .when(movieService).disableMovie(999L);

        // Act & Assert
        mockMvc.perform(delete("/movies/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));

        verify(movieService, times(1)).disableMovie(999L);
    }
}
