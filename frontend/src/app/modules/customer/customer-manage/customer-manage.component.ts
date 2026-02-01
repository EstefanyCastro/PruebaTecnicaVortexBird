import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { CustomerService } from '@core/services/customer.service';
import { Customer } from '@models/customer.model';

@Component({
  selector: 'app-customer-manage',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './customer-manage.component.html',
  styleUrl: './customer-manage.component.css'
})
export class CustomerManageComponent implements OnInit {
  customers: Customer[] = [];
  loading = false;
  errorMessage = '';
  customerToToggle: Customer | null = null;
  showConfirmation = false;

  constructor(
    private customerService: CustomerService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCustomers();
  }

  loadCustomers(): void {
    this.loading = true;
    this.errorMessage = '';
    
    this.customerService.getAllCustomers().subscribe({
      next: (response) => {
        if (response.success) {
          this.customers = response.data;
          this.loading = false;
        }
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error.error?.message || 'Error al cargar clientes';
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/home']);
  }

  prepareToggle(customer: Customer): void {
    this.customerToToggle = customer;
    this.showConfirmation = true;
  }

  cancelToggle(): void {
    this.customerToToggle = null;
    this.showConfirmation = false;
  }

  confirmToggle(): void {
    if (!this.customerToToggle) return;

    const customer = this.customerToToggle;
    this.customerService.disableCustomer(customer.id).subscribe({
      next: (response) => {
        if (response.success) {
          this.showConfirmation = false;
          this.customerToToggle = null;
          this.loadCustomers();
        }
      },
      error: (error) => {
        this.errorMessage = error.error?.message || 'Error al cambiar estado del cliente';
        this.showConfirmation = false;
        this.customerToToggle = null;
      }
    });
  }

  getStatusBadgeClass(enabled: boolean): string {
    return enabled ? 'badge bg-success' : 'badge bg-secondary';
  }

  getStatusText(enabled: boolean): string {
    return enabled ? 'Activo' : 'Inactivo';
  }

  getRoleBadgeClass(role: string): string {
    return role === 'ADMIN' ? 'badge bg-primary' : 'badge bg-info';
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('es-CO', { 
      year: 'numeric', 
      month: 'short', 
      day: 'numeric' 
    });
  }
}
