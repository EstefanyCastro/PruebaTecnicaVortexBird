import { Injectable } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

/**
 * Guard to protect authenticated routes.
 * 
 * Ensures that only authenticated users can access certain routes.
 */
export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);

  router.navigate(['/auth/login']);
  return false;
};
