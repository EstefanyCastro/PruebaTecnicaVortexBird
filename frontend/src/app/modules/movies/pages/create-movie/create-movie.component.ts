import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MovieService } from '@core/services/movie.service';
import { CreateMovieRequest } from '@models/movie.model';

/**
 * Component for creating and editing movies.
 * Provides a form for entering movie details.
 */
@Component({
  selector: 'app-create-movie',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-movie.component.html',
  styleUrls: ['./create-movie.component.css']
})
export class CreateMovieComponent implements OnInit {
  movieForm: FormGroup;
  loading = false;
  error: string | null = null;
  success: string | null = null;
  isEditMode = false;
  movieId: number | null = null;
  pageTitle = 'Crear Película';

  constructor(
    private fb: FormBuilder,
    private movieService: MovieService,
    private router: Router,
    private route: ActivatedRoute
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

  ngOnInit(): void {
    // Verificar si estamos en modo edición
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.movieId = parseInt(id);
      this.pageTitle = 'Editar Película';
      this.loadMovie(this.movieId);
    }
  }

  /**
   * Carga los datos de la película para editar
   */
  loadMovie(id: number): void {
    this.loading = true;
    this.movieService.getMovieById(id).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.movieForm.patchValue({
            title: response.data.title,
            description: response.data.description,
            imageUrl: response.data.imageUrl,
            genre: response.data.genre,
            duration: response.data.duration,
            price: response.data.price
          });
        }
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.error = 'Error al cargar la película';
        console.error('Error loading movie:', err);
      }
    });
  }

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

    const operation = this.isEditMode && this.movieId
      ? this.movieService.updateMovie(this.movieId, movieData)
      : this.movieService.createMovie(movieData);

    operation.subscribe({
      next: (response) => {
        this.loading = false;
        this.success = this.isEditMode ? '¡Película actualizada exitosamente!' : '¡Película creada exitosamente!';
        setTimeout(() => {
          this.router.navigate(['/movies/manage']);
        }, 2000);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message || `Error al ${this.isEditMode ? 'actualizar' : 'crear'} la película. Por favor, intente nuevamente.`;
        console.error('Error with movie operation:', err);
      }
    });
  }

  resetForm(): void {
    this.movieForm.reset();
    this.error = null;
    this.success = null;
  }

  cancel(): void {
    this.router.navigate(['/movies/manage']);
  }
}
