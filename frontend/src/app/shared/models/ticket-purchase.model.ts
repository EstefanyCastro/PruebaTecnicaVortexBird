/**
 * Ticket Purchase models.
 * 
 * Defines interfaces for ticket purchase operations.
 */

export interface PaymentInfo {
  cardNumber: string;
  cardHolderName: string;
  expiryDate: string;
  cvv: string;
}

export interface CreateTicketPurchase {
  movieId: number;
  quantity: number;
  paymentInfo: PaymentInfo;
}

export enum PurchaseStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  CANCELLED = 'CANCELLED',
  REFUNDED = 'REFUNDED'
}

export interface TicketPurchase {
  id: number;
  customerId: number;
  customerEmail: string;
  customerName: string;
  movieId: number;
  movieTitle: string;
  quantity: number;
  unitPrice: number;
  totalAmount: number;
  status: PurchaseStatus;
  cardLastFour: string;
  cardHolderName: string;
  purchaseDate: string;
  confirmationCode: string;
}
