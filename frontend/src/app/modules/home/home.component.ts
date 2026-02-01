import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MovieService } from '@core/services/movie.service';
import { Movie } from '@models/movie.model';

/**
 * Home component.
 * 
 * Main landing page with movie search and filtering.
 */
@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  searchTerm: string = '';
  selectedGenre: string = '';
  movies: Movie[] = [];
  genres: string[] = [];
  loading: boolean = false;

  constructor(private movieService: MovieService) {}

  ngOnInit(): void {
    this.loadMovies();
  }

  loadMovies(): void {
    this.loading = true;
    this.movieService.getAllMovies().subscribe({
      next: (response) => {
        this.movies = response.data;
        this.extractGenres();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error al cargar películas:', error);
        this.loading = false;
      }
    });
  }

  extractGenres(): void {
    const uniqueGenres = new Set<string>();
    this.movies.forEach(movie => {
      if (movie.genre) {
        uniqueGenres.add(movie.genre);
      }
    });
    this.genres = Array.from(uniqueGenres).sort();
  }

  filterMovies(): void {
    this.loading = true;
    const title = this.searchTerm.trim() || undefined;
    const genre = this.selectedGenre || undefined;
    
    this.movieService.searchMovies(title, genre).subscribe({
      next: (response) => {
        this.movies = response.data;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error al filtrar películas:', error);
        this.loading = false;
      }
    });
  }

  selectGenre(genre: string): void {
    this.selectedGenre = this.selectedGenre === genre ? '' : genre;
    this.filterMovies();
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.selectedGenre = '';
    this.loadMovies();
  }
}
