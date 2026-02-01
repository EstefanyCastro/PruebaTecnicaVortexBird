import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { AuthService } from '@core/services/auth.service';
import { LoginRequest } from '@models/customer.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  loading = false;
  errorMessage = '';
  showPassword = false;
  returnUrl: string = '/home';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    // Capturar returnUrl de los query params
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/home';

    if (this.authService.isLoggedIn()) {
      this.router.navigate([this.returnUrl]);
      return;
    }

    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      Object.keys(this.loginForm.controls).forEach(key => {
        this.loginForm.controls[key].markAsTouched();
      });
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const request: LoginRequest = {
      email: this.loginForm.value.email,
      password: this.loginForm.value.password
    };

    this.authService.login(request).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.loading = false;
          
          // Redirigir a returnUrl o home según el rol
          if (response.data.role === 'ADMIN' && this.returnUrl !== '/home') {
            // Admin puede ir a la URL solicitada solo si no es home por defecto
            this.router.navigateByUrl(this.returnUrl);
          } else if (response.data.role === 'CUSTOMER') {
            // Customer siempre usa returnUrl
            this.router.navigateByUrl(this.returnUrl);
          } else {
            this.router.navigate(['/home']);
          }
        }
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error.error?.message || 'Credenciales inválidas. Por favor intenta de nuevo.';
      }
    });
  }

  getErrorMessage(field: string): string {
    const control = this.loginForm.get(field);
    if (!control?.touched || !control?.errors) return '';

    const errors = control.errors;
    if (errors['required']) return 'Este campo es requerido';
    if (errors['email']) return 'Email inválido';
    if (errors['minlength']) return `Mínimo ${errors['minlength'].requiredLength} caracteres`;
    return '';
  }
}
