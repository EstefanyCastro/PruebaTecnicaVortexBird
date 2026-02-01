import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MovieService } from '@core/services/movie.service';
import { Movie } from '@models/movie.model';

@Component({
  selector: 'app-movie-manage',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './movie-manage.component.html',
  styleUrl: './movie-manage.component.css'
})
export class MovieManageComponent implements OnInit {
  movies: Movie[] = [];
  loading = false;
  errorMessage = '';
  movieToToggle: Movie | null = null;
  showConfirmation = false;

  constructor(
    private movieService: MovieService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadMovies();
  }

  loadMovies(): void {
    this.loading = true;
    this.errorMessage = '';
    
    this.movieService.getAllMovies().subscribe({
      next: (response) => {
        if (response.success) {
          this.movies = response.data;
          this.loading = false;
        }
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error.error?.message || 'Error al cargar películas';
      }
    });
  }

  createMovie(): void {
    this.router.navigate(['/movies/manage/create']);
  }

  editMovie(id: number): void {
    this.router.navigate(['/movies/manage/edit', id]);
  }

  goBack(): void {
    this.router.navigate(['/home']);
  }

  prepareToggle(movie: Movie): void {
    this.movieToToggle = movie;
    this.showConfirmation = true;
  }

  cancelToggle(): void {
    this.movieToToggle = null;
    this.showConfirmation = false;
  }

  confirmToggle(): void {
    if (!this.movieToToggle) return;

    const movie = this.movieToToggle;
    this.movieService.disableMovie(movie.id).subscribe({
      next: (response) => {
        if (response.success) {
          this.showConfirmation = false;
          this.movieToToggle = null;
          this.loadMovies();
        }
      },
      error: (error) => {
        this.errorMessage = error.error?.message || 'Error al cambiar estado de película';
        this.showConfirmation = false;
        this.movieToToggle = null;
      }
    });
  }

  toggleMovie(movie: Movie): void {
    const action = movie.isEnabled ? 'inhabilitar' : 'habilitar';
    if (confirm(`¿Estás seguro de ${action} la película "${movie.title}"?`)) {
      this.movieService.disableMovie(movie.id).subscribe({
        next: (response) => {
          if (response.success) {
            // Recargar lista después de la acción
            this.loadMovies();
          }
        },
        error: (error) => {
          alert(error.error?.message || `Error al ${action} película`);
        }
      });
    }
  }

  getStatusBadgeClass(enabled: boolean): string {
    return enabled ? 'badge bg-success' : 'badge bg-secondary';
  }

  getStatusText(enabled: boolean): string {
    return enabled ? 'Habilitada' : 'Inhabilitada';
  }
}
