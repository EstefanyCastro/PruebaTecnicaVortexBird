import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { TicketPurchaseService } from '@core/services/ticket-purchase.service';
import { MovieService } from '@core/services/movie.service';
import { CustomerService } from '@core/services/customer.service';
import { TicketPurchase } from '@models/ticket-purchase.model';
import { Movie } from '@models/movie.model';
import { Customer } from '@models/customer.model';

/**
 * Purchase Management Component.
 * 
 * Admin interface for viewing and managing all ticket purchases.
 */
@Component({
  selector: 'app-purchase-manage',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './purchase-manage.component.html',
  styleUrls: ['./purchase-manage.component.css']
})
export class PurchaseManageComponent implements OnInit {
  purchases: TicketPurchase[] = [];
  filteredPurchases: TicketPurchase[] = [];
  movies: Movie[] = [];
  customers: Customer[] = [];
  loading = false;
  errorMessage = '';
  
  // Filters
  selectedMovieId: number | null = null;
  selectedCustomerId: number | null = null;
  selectedStatus: string = '';
  searchTerm: string = '';

  constructor(
    private purchaseService: TicketPurchaseService,
    private movieService: MovieService,
    private customerService: CustomerService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.errorMessage = '';

    // Load movies for filter
    this.movieService.getAllMovies().subscribe({
      next: (response) => {
        if (response.success) {
          this.movies = response.data;
        }
      },
      error: (error) => {
        console.error('Error loading movies:', error);
      }
    });

    // Load customers for filter
    this.customerService.getAllCustomers().subscribe({
      next: (response) => {
        if (response.success) {
          this.customers = response.data;
        }
      },
      error: (error) => {
        console.error('Error loading customers:', error);
      }
    });

    // Load all purchases by loading from first movie (simplified approach)
    // In a real scenario, you'd have an endpoint to get all purchases
    this.loadAllPurchases();
  }

  loadAllPurchases(): void {
    // Since we don't have a getAll endpoint, we'll load by movie
    if (this.movies.length > 0) {
      const purchasePromises = this.movies.map(movie => 
        this.purchaseService.getMoviePurchases(movie.id).toPromise()
      );

      Promise.all(purchasePromises).then(responses => {
        const allPurchases: TicketPurchase[] = [];
        responses.forEach(response => {
          if (response && response.success) {
            allPurchases.push(...response.data);
          }
        });
        
        // Remove duplicates by id
        const uniquePurchases = allPurchases.filter((purchase, index, self) =>
          index === self.findIndex(p => p.id === purchase.id)
        );
        
        this.purchases = uniquePurchases.sort((a, b) => 
          new Date(b.purchaseDate).getTime() - new Date(a.purchaseDate).getTime()
        );
        this.filteredPurchases = [...this.purchases];
        this.loading = false;
      }).catch(error => {
        console.error('Error loading purchases:', error);
        this.errorMessage = 'Error al cargar las compras';
        this.loading = false;
      });
    } else {
      // Retry after movies are loaded
      setTimeout(() => {
        if (this.movies.length > 0) {
          this.loadAllPurchases();
        } else {
          this.loading = false;
        }
      }, 1000);
    }
  }

  applyFilters(): void {
    this.filteredPurchases = this.purchases.filter(purchase => {
      // Movie filter
      if (this.selectedMovieId && purchase.movieId !== this.selectedMovieId) {
        return false;
      }

      // Customer filter
      if (this.selectedCustomerId && purchase.customerId !== this.selectedCustomerId) {
        return false;
      }

      // Status filter
      if (this.selectedStatus && purchase.status !== this.selectedStatus) {
        return false;
      }

      // Search term (customer name, email, or movie title)
      if (this.searchTerm) {
        const term = this.searchTerm.toLowerCase();
        return (
          purchase.customerName.toLowerCase().includes(term) ||
          purchase.customerEmail.toLowerCase().includes(term) ||
          purchase.movieTitle.toLowerCase().includes(term) ||
          purchase.confirmationCode.toLowerCase().includes(term)
        );
      }

      return true;
    });
  }

  clearFilters(): void {
    this.selectedMovieId = null;
    this.selectedCustomerId = null;
    this.selectedStatus = '';
    this.searchTerm = '';
    this.filteredPurchases = [...this.purchases];
  }

  goBack(): void {
    this.router.navigate(['/home']);
  }

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'CONFIRMED': return 'badge bg-success';
      case 'PENDING': return 'badge bg-warning';
      case 'CANCELLED': return 'badge bg-danger';
      case 'REFUNDED': return 'badge bg-secondary';
      default: return 'badge bg-secondary';
    }
  }

  getStatusText(status: string): string {
    switch (status) {
      case 'CONFIRMED': return 'Confirmada';
      case 'PENDING': return 'Pendiente';
      case 'CANCELLED': return 'Cancelada';
      case 'REFUNDED': return 'Reembolsada';
      default: return status;
    }
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('es-CO', { 
      year: 'numeric', 
      month: 'short', 
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  getTotalRevenue(): number {
    return this.filteredPurchases.reduce((sum, purchase) => sum + purchase.totalAmount, 0);
  }

  getTotalTickets(): number {
    return this.filteredPurchases.reduce((sum, purchase) => sum + purchase.quantity, 0);
  }
}
