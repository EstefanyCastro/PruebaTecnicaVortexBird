import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '@core/services/auth.service';

/**
 * Guard to protect customer-only routes.
 * 
 * Ensures that only users with CUSTOMER role can access certain routes.
 * Redirects non-customer users to home page.
 */
export const customerGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isCustomer()) {
    return true;
  }

  // Redirect to home if not customer
  router.navigate(['/home']);
  return false;
};
