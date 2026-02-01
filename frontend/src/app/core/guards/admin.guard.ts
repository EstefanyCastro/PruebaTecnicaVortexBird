import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '@core/services/auth.service';

/**
 * Guard to protect admin-only routes.
 * 
 * Ensures that only users with ADMIN role can access certain routes.
 * Redirects non-admin users to home page.
 */
export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAdmin()) {
    return true;
  }

  // Redirect to home if not admin
  router.navigate(['/home']);
  return false;
};
