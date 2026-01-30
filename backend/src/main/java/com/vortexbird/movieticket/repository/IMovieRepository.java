package com.vortexbird.movieticket.repository;

import com.vortexbird.movieticket.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Movie entity.
 *
 * Provides database access operations for Movie entities using Spring Data JPA.
 */
@Repository
public interface IMovieRepository extends JpaRepository<Movie, Long> {
}


//     Optional<Movie> findByIdAndIsEnabledTrue(Long id);

//     @Query("SELECT m FROM Movie m WHERE m.isEnabled = true")
//     List<Movie> findAllEnabled();

//     @Query("SELECT m FROM Movie m WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :name, '%')) AND m.isEnabled = true")
//     List<Movie> findByTitle(@Param("name") String title);

//     @Query("SELECT m FROM Movie m WHERE LOWER(m.genre) = LOWER(:genre) AND m.isEnabled = true")
//     List<Movie> findByGenre(@Param("genre") String genre);

