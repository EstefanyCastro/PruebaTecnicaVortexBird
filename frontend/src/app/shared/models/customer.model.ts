/**
 * Customer model representing a user in the system.
 */
export interface Customer {
  id: number;
  email: string;
  phone: string;
  firstName: string;
  lastName: string;
  role: 'CUSTOMER' | 'ADMIN';
  enabled: boolean;
  createdAt: string;
}

/**
 * Login response containing user information and authentication token.
 */
export interface LoginResponse {
  customerId: number;
  email: string;
  firstName: string;
  lastName: string;
  role: 'CUSTOMER' | 'ADMIN';
}

/**
 * Register request for creating a new customer account.
 */
export interface RegisterCustomerRequest {
  email: string;
  phone: string;
  firstName: string;
  lastName: string;
  password: string;
}

/**
 * Login request credentials.
 */
export interface LoginRequest {
  email: string;
  password: string;
}
