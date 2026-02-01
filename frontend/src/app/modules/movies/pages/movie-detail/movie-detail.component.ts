import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MovieService } from '@core/services/movie.service';
import { AuthService } from '@core/services/auth.service';
import { Movie } from '@models/movie.model';

@Component({
  selector: 'app-movie-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './movie-detail.component.html',
  styleUrl: './movie-detail.component.css'
})
export class MovieDetailComponent implements OnInit {
  movie: Movie | null = null;
  loading = false;
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private movieService: MovieService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadMovie(+id);
    }
  }

  loadMovie(id: number): void {
    this.loading = true;
    this.movieService.getMovieById(id).subscribe({
      next: (response) => {
        if (response.success) {
          this.movie = response.data;
        }
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = error.error?.message || 'Error al cargar película';
        this.loading = false;
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/home']);
  }

  handlePurchase(): void {
    if (!this.authService.isLoggedIn()) {
      // Si no está logueado, redirigir a login con returnUrl
      this.router.navigate(['/auth/login'], {
        queryParams: { returnUrl: `/purchase/${this.movie?.id}` }
      });
    } else {
      // Si está logueado, ir a página de compra con el ID de la película
      this.router.navigate(['/purchase', this.movie?.id]);
    }
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }
}

