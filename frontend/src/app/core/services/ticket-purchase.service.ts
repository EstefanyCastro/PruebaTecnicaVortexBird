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

  createPurchase(customerId: number, purchaseData: CreateTicketPurchase): Observable<ApiResponse<TicketPurchase>> {
    const params = new HttpParams().set('customerId', customerId.toString());
    return this.http.post<ApiResponse<TicketPurchase>>(`${this.apiUrl}`, purchaseData, { params });
  }

  getPurchaseById(id: number): Observable<ApiResponse<TicketPurchase>> {
    return this.http.get<ApiResponse<TicketPurchase>>(`${this.apiUrl}/${id}`);
  }

  getCustomerPurchases(customerId: number): Observable<ApiResponse<TicketPurchase[]>> {
    return this.http.get<ApiResponse<TicketPurchase[]>>(`${this.apiUrl}/customer/${customerId}`);
  }

  getMoviePurchases(movieId: number): Observable<ApiResponse<TicketPurchase[]>> {
    return this.http.get<ApiResponse<TicketPurchase[]>>(`${this.apiUrl}/movie/${movieId}`);
  }
}
