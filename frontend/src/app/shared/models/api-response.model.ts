/**
 * Generic API response wrapper.
 * Standardizes all API responses from the backend.
 */
export interface ApiResponse<T> {
  data: T;
  message: string;
  success: boolean;
}
