import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

/**
 * HTTP Interceptor for handling errors.
 * 
 * Globally handles HTTP errors and provides consistent error handling.
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    catchError((error) => {
      // Handle different HTTP error codes
      switch (error.status) {
        case 401:
          console.error('Unauthorized: Authentication required');
          break;
        case 403:
          console.error('Forbidden: Access denied');
          break;
        case 404:
          console.error('Not found');
          break;
        case 500:
          console.error('Server error');
          break;
        default:
          console.error('An error occurred:', error.message);
      }
      return throwError(() => error);
    })
  );
};
