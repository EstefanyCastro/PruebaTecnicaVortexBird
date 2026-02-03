import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CustomerService } from '@core/services/customer.service';
import { Customer } from '@models/customer.model';

@Component({
  selector: 'app-customer-manage',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './customer-manage.component.html',
  styleUrl: './customer-manage.component.css'
})
export class CustomerManageComponent implements OnInit {
  customers: Customer[] = [];
  filteredCustomers: Customer[] = [];
  searchTerm: string = '';
  loading = false;
  errorMessage = '';
  customerToToggle: Customer | null = null;
  showConfirmation = false;
  
  // Pagination
  currentPage = 1;
  itemsPerPage = 10;
  totalPages = 1;
  Math = Math;

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
          this.filteredCustomers = [...this.customers];
          this.updateTotalPages();
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

  filterCustomers(): void {
    if (!this.searchTerm.trim()) {
      this.filteredCustomers = [...this.customers];
    } else {
      const term = this.searchTerm.toLowerCase().trim();
      this.filteredCustomers = this.customers.filter(customer =>
        customer.firstName.toLowerCase().includes(term) ||
        customer.lastName.toLowerCase().includes(term) ||
        customer.email.toLowerCase().includes(term)
      );
    }
    this.currentPage = 1;
    this.updateTotalPages();
  }

  clearSearch(): void {
    this.searchTerm = '';
    this.filterCustomers();
  }

  // Pagination methods
  get paginatedCustomers(): Customer[] {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    return this.filteredCustomers.slice(startIndex, endIndex);
  }

  updateTotalPages(): void {
    this.totalPages = Math.ceil(this.filteredCustomers.length / this.itemsPerPage);
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  }

  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  get pageNumbers(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i + 1);
  }
}
