import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MovieService } from '@core/services/movie.service';
import { StorageService } from '@core/services/storage.service';
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
  
  // Image upload properties
  selectedFile: File | null = null;
  imagePreview: string | null = null;

  constructor(
    private fb: FormBuilder,
    private movieService: MovieService,
    private storageService: StorageService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.movieForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(255)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      genre: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(100)]],
      duration: ['', [Validators.required, Validators.min(1)]],
      price: ['', [Validators.required, Validators.min(0)]]
    });
  }

  /**
   * Maneja la selección de imagen
   */
  onImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      
      // Validar archivo
      const validation = this.storageService.validateImage(file);
      if (!validation.valid) {
        this.error = validation.error || 'Archivo no válido';
        this.selectedFile = null;
        this.imagePreview = null;
        return;
      }

      this.selectedFile = file;
      this.error = null;

      // Crear preview
      this.storageService.createImagePreview(file).then(preview => {
        this.imagePreview = preview;
      }).catch(err => {
        console.error('Error creating preview:', err);
        this.error = 'Error al crear vista previa de la imagen';
      });
    }
  }

  /**
   * Elimina la imagen seleccionada
   */
  removeImage(): void {
    this.selectedFile = null;
    this.imagePreview = null;
    
    // Limpiar el input file
    const fileInput = document.getElementById('imageInput') as HTMLInputElement;
    if (fileInput) {
      fileInput.value = '';
    }
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
            genre: response.data.genre,
            duration: response.data.duration,
            price: response.data.price
          });
          
          // Si tiene imagen, mostrarla en el preview
          if (response.data.imageUrl) {
            this.imagePreview = response.data.imageUrl;
          }
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

    if (!this.isEditMode && !this.selectedFile) {
      this.error = 'Por favor, seleccione una imagen para la película.';
      return;
    }

    this.loading = true;
    this.error = null;
    this.success = null;

    const formData = new FormData();
    formData.append('title', this.movieForm.value.title);
    formData.append('description', this.movieForm.value.description);
    formData.append('genre', this.movieForm.value.genre);
    formData.append('duration', this.movieForm.value.duration.toString());
    formData.append('price', this.movieForm.value.price.toString());
    
    if (this.selectedFile) {
      formData.append('image', this.selectedFile);
    }
    
    // Si estamos editando y hay una URL existente, enviarla
    if (this.isEditMode && this.imagePreview && !this.selectedFile) {
      formData.append('imageUrl', this.imagePreview);
    }

    const operation = this.isEditMode && this.movieId
      ? this.movieService.updateMovieWithImage(this.movieId, formData)
      : this.movieService.createMovieWithImage(formData);

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
