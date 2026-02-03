import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TicketPurchaseService } from '../../../core/services/ticket-purchase.service';
import { AuthService } from '../../../core/services/auth.service';
import { TicketPurchase } from '../../../shared/models/ticket-purchase.model';

@Component({
  selector: 'app-customer-purchases',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './customer-purchases.component.html',
  styleUrl: './customer-purchases.component.css'
})
export class CustomerPurchasesComponent implements OnInit {
  purchases: TicketPurchase[] = [];
  isLoading = true;
  errorMessage = '';
  customerId: number = 0;

  constructor(
    private purchaseService: TicketPurchaseService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const user = this.authService.getCurrentUser();
    if (user && user.customerId) {
      this.customerId = user.customerId;
      this.loadPurchases();
    }
  }

  loadPurchases(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.purchaseService.getCustomerPurchases(this.customerId).subscribe({
      next: (response) => {
        this.purchases = response.data || [];
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading purchases:', error);
        this.errorMessage = 'Error al cargar las compras. Por favor, intenta de nuevo.';
        this.isLoading = false;
      }
    });
  }

  cancelPurchase(purchaseId: number): void {
    if (!confirm('¿Estás seguro de que deseas cancelar esta compra?')) {
      return;
    }

    this.purchaseService.cancelPurchase(purchaseId).subscribe({
      next: (response) => {
        alert('Compra cancelada exitosamente');
        this.loadPurchases(); // Recargar la lista
      },
      error: (error) => {
        console.error('Error canceling purchase:', error);
        alert('Error al cancelar la compra. Por favor, intenta de nuevo.');
      }
    });
  }

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'CONFIRMED':
        return 'badge bg-success';
      case 'CANCELLED':
        return 'badge bg-danger';
      case 'PENDING':
        return 'badge bg-warning';
      default:
        return 'badge bg-secondary';
    }
  }

  getStatusText(status: string): string {
    switch (status) {
      case 'CONFIRMED':
        return 'Confirmada';
      case 'CANCELLED':
        return 'Cancelada';
      case 'PENDING':
        return 'Pendiente';
      default:
        return status;
    }
  }

  canCancelPurchase(purchase: TicketPurchase): boolean {
    return purchase.status === 'CONFIRMED';
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('es-ES', { 
      year: 'numeric', 
      month: 'long', 
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0
    }).format(amount);
  }
}
