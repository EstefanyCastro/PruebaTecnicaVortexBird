import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MovieService } from '@core/services/movie.service';
import { CreateMovieRequest } from '@models/movie.model';

/**
 * Component for creating new movies.
 * Provides a form for entering movie details.
 */
@Component({
  selector: 'app-create-movie',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-movie.component.html',
  styleUrls: ['./create-movie.component.css']
})
export class CreateMovieComponent {
  movieForm: FormGroup;
  loading = false;
  error: string | null = null;
  success: string | null = null;

  constructor(
    private fb: FormBuilder,
    private movieService: MovieService,
    private router: Router
  ) {
    this.movieForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(255)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      imageUrl: ['', Validators.required],
      genre: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(100)]],
      duration: ['', [Validators.required, Validators.min(1)]],
      price: ['', [Validators.required, Validators.min(0)]]
    });
  }

  /**
   * Handles form submission
   */
  onSubmit(): void {
    if (this.movieForm.invalid) {
      this.error = 'Por favor, complete todos los campos requeridos correctamente.';
      return;
    }

    this.loading = true;
    this.error = null;
    this.success = null;

    const movieData: CreateMovieRequest = {
      title: this.movieForm.value.title,
      description: this.movieForm.value.description,
      imageUrl: this.movieForm.value.imageUrl,
      genre: this.movieForm.value.genre,
      duration: parseInt(this.movieForm.value.duration),
      price: parseFloat(this.movieForm.value.price)
    };

    this.movieService.createMovie(movieData).subscribe({
      next: (response) => {
        this.loading = false;
        this.success = '¡Película creada exitosamente!';
        setTimeout(() => {
          this.router.navigate(['/movies/list']);
        }, 2000);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message || 'Error al crear la película. Por favor, intente nuevamente.';
        console.error('Error creating movie:', err);
      }
    });
  }

  /**
   * Resets the form
   */
  resetForm(): void {
    this.movieForm.reset();
    this.error = null;
    this.success = null;
  }

  /**
   * Navigates back to movie list
   */
  cancel(): void {
    this.router.navigate(['/movies/list']);
  }
}
