import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MovieService } from '@core/services/movie.service';
import { AuthService } from '@core/services/auth.service';
import { TicketPurchaseService } from '@core/services/ticket-purchase.service';
import { Movie } from '@models/movie.model';
import { CreateTicketPurchase, TicketPurchase } from '@models/ticket-purchase.model';

/**
 * Purchase component.
 * 
 * Handles the ticket purchase process for a selected movie.
 * Allows users to select quantity, enter payment information, and confirm purchase.
 */
@Component({
  selector: 'app-purchase',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-purchase.component.html',
  styleUrls: ['./create-purchase.component.css']
})
export class PurchaseComponent implements OnInit {
  purchaseForm!: FormGroup;
  movie: Movie | null = null;
  completedPurchase: TicketPurchase | null = null;
  loading: boolean = false;
  errorMessage: string = '';
  quantity: number = 1;
  currentStep: number = 1; // 1: Quantity, 2: Payment, 3: Confirmation

  constructor(
    private fb: FormBuilder,
    private movieService: MovieService,
    private authService: AuthService,
    private ticketPurchaseService: TicketPurchaseService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    // Verificar autenticación
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/auth/login']);
      return;
    }

    // Obtener ID de la película desde la ruta
    const movieId = this.route.snapshot.paramMap.get('id');
    if (movieId) {
      this.loadMovie(parseInt(movieId));
    } else {
      this.router.navigate(['/home']);
    }

    this.initForm();
  }

  initForm(): void {
    this.purchaseForm = this.fb.group({
      // Quantity
      quantity: [1, [Validators.required, Validators.min(1), Validators.max(10)]],
      
      // Payment information
      cardNumber: ['', [Validators.required, Validators.pattern(/^\d{16}$/)]],
      cardHolder: ['', [Validators.required, Validators.minLength(3)]],
      expiryMonth: ['', [Validators.required, Validators.pattern(/^(0[1-9]|1[0-2])$/)]],
      expiryYear: ['', [Validators.required, Validators.pattern(/^\d{2}$/)]],
      cvv: ['', [Validators.required, Validators.pattern(/^\d{3,4}$/)]]
    });

    // Escuchar cambios en la cantidad
    this.purchaseForm.get('quantity')?.valueChanges.subscribe(value => {
      this.quantity = value;
    });
  }

  loadMovie(id: number): void {
    this.loading = true;
    this.movieService.getMovieById(id).subscribe({
      next: (response) => {
        this.movie = response.data;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error al cargar película:', error);
        this.errorMessage = 'No se pudo cargar la información de la película';
        this.loading = false;
      }
    });
  }

  getTotalPrice(): number {
    if (!this.movie) return 0;
    return this.movie.price * this.quantity;
  }

  goToStep(step: number): void {
    if (step === 2) {
      // Validar cantidad antes de ir al paso de pago
      if (this.purchaseForm.get('quantity')?.invalid) {
        return;
      }
    }
    this.currentStep = step;
  }

  onSubmit(): void {
    if (this.purchaseForm.invalid || !this.movie) {
      Object.keys(this.purchaseForm.controls).forEach(key => {
        this.purchaseForm.get(key)?.markAsTouched();
      });
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    // Obtener ID del usuario autenticado
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser || !currentUser.customerId) {
      this.errorMessage = 'Error: Usuario no identificado';
      this.loading = false;
      return;
    }

    // Preparar datos de compra
    const expiryDate = `${this.purchaseForm.value.expiryMonth}/${this.purchaseForm.value.expiryYear}`;
    
    const purchaseData: CreateTicketPurchase = {
      movieId: this.movie.id,
      quantity: this.purchaseForm.value.quantity,
      paymentInfo: {
        cardNumber: this.purchaseForm.value.cardNumber,
        cardHolderName: this.purchaseForm.value.cardHolder.toUpperCase(),
        expiryDate: expiryDate,
        cvv: this.purchaseForm.value.cvv
      }
    };

    // Call purchase service
    this.ticketPurchaseService.createPurchase(currentUser.customerId, purchaseData).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.completedPurchase = response.data;
          this.loading = false;
          this.currentStep = 3;
        }
      },
      error: (error) => {
        console.error('Error processing purchase:', error);
        this.errorMessage = error.error?.message || 'Error processing the purchase. Please try again.';
        this.loading = false;
      }
    });
  }

  formatCardNumber(event: any): void {
    let value = event.target.value.replace(/\s/g, '');
    if (value.length > 16) {
      value = value.slice(0, 16);
    }
    this.purchaseForm.patchValue({ cardNumber: value }, { emitEvent: false });
  }

  goBack(): void {
    if (this.movie) {
      this.router.navigate(['/movies', this.movie.id]);
    } else {
      this.router.navigate(['/home']);
    }
  }

  goToHome(): void {
    this.router.navigate(['/home']);
  }

  getErrorMessage(field: string): string {
    const control = this.purchaseForm.get(field);
    if (!control?.errors) return '';

    if (control.errors['required']) return 'Este campo es requerido';
    if (control.errors['min']) return 'La cantidad mínima es 1';
    if (control.errors['max']) return 'La cantidad máxima es 10';
    if (control.errors['pattern']) {
      switch (field) {
        case 'cardNumber': return 'Número de tarjeta inválido (16 dígitos)';
        case 'expiryMonth': return 'Mes inválido (01-12)';
        case 'expiryYear': return 'Año inválido (2 dígitos)';
        case 'cvv': return 'CVV inválido (3-4 dígitos)';
        default: return 'Formato inválido';
      }
    }
    if (control.errors['minlength']) return `Mínimo ${control.errors['minlength'].requiredLength} caracteres`;

    return 'Error de validación';
  }
}
