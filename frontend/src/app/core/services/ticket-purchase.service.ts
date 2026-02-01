import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '@models/api-response.model';
import { CreateTicketPurchase, TicketPurchase } from '@models/ticket-purchase.model';

/**
 * Service for ticket purchase operations.
 * 
 * Handles all HTTP requests related to ticket purchases.
 */
@Injectable({
  providedIn: 'root'
})
export class TicketPurchaseService {
  private apiUrl = '/api/purchases';

  constructor(private http: HttpClient) {}

  /**
   * Create a new ticket purchase.
   * 
   * @param customerId - ID of the customer making the purchase
   * @param purchaseData - Purchase details including movie, quantity, and payment info
   * @returns Observable with the created purchase
   */
  createPurchase(customerId: number, purchaseData: CreateTicketPurchase): Observable<ApiResponse<TicketPurchase>> {
    const params = new HttpParams().set('customerId', customerId.toString());
    return this.http.post<ApiResponse<TicketPurchase>>(`${this.apiUrl}`, purchaseData, { params });
  }

  /**
   * Get a specific purchase by ID.
   * 
   * @param id - Purchase ID
   * @returns Observable with the purchase details
   */
  getPurchaseById(id: number): Observable<ApiResponse<TicketPurchase>> {
    return this.http.get<ApiResponse<TicketPurchase>>(`${this.apiUrl}/${id}`);
  }

  /**
   * Get all purchases for a specific customer.
   * 
   * @param customerId - Customer ID
   * @returns Observable with list of customer purchases
   */
  getCustomerPurchases(customerId: number): Observable<ApiResponse<TicketPurchase[]>> {
    return this.http.get<ApiResponse<TicketPurchase[]>>(`${this.apiUrl}/customer/${customerId}`);
  }

  /**
   * Get all purchases for a specific movie (admin only).
   * 
   * @param movieId - Movie ID
   * @returns Observable with list of movie purchases
   */
  getMoviePurchases(movieId: number): Observable<ApiResponse<TicketPurchase[]>> {
    return this.http.get<ApiResponse<TicketPurchase[]>>(`${this.apiUrl}/movie/${movieId}`);
  }
}
