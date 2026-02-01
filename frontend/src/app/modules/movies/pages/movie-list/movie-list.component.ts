import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MovieService } from '@core/services/movie.service';
import { Movie } from '@models/movie.model';

/**
 * Component for displaying list of movies.
 */
@Component({
  selector: 'app-movie-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './movie-list.component.html',
  styleUrls: ['./movie-list.component.css']
})
export class MovieListComponent implements OnInit {
  movies: Movie[] = [];
  loading = false;
  error: string | null = null;

  constructor(private movieService: MovieService) {}

  ngOnInit(): void {
    this.loadMovies();
  }

  loadMovies(): void {
    this.loading = true;
    this.error = null;

    this.movieService.getAllMovies().subscribe({
      next: (response) => {
        this.movies = response.data || [];
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Error al cargar las películas';
        this.loading = false;
        console.error('Error loading movies:', err);
      }
    });
  }
}
