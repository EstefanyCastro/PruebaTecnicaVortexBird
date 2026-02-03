import { Routes } from '@angular/router';
import { adminGuard } from '@core/guards/admin.guard';
import { customerGuard } from '@core/guards/customer.guard';

/**
 * Application routes configuration.
 * 
 * Defines all routes for the application including lazy-loaded modules.
 */
export const routes: Routes = [
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full'
  },
  {
    path: 'home',
    loadComponent: () => import('@modules/home/home.component').then(m => m.HomeComponent)
  },
  {
    path: 'movies',
    loadChildren: () => import('@modules/movies/movies.routes').then(m => m.MOVIES_ROUTES)
  },
  {
    path: 'admin/customers',
    canActivate: [adminGuard],
    loadComponent: () => import('@modules/customer/customer-manage/customer-manage.component').then(m => m.CustomerManageComponent)
  },
  {
    path: 'admin/purchases',
    canActivate: [adminGuard],
    loadComponent: () => import('@app/modules/purchase/pages/purchase-manage/purchase-manage.component').then(m => m.PurchaseManageComponent)
  },
  {
    path: 'customer/purchases',
    canActivate: [customerGuard],
    loadComponent: () => import('@modules/customer/customer-purchases/customer-purchases.component').then(m => m.CustomerPurchasesComponent)
  },
  {
    path: 'auth/register',
    loadComponent: () => import('@modules/auth/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'auth/login',
    loadComponent: () => import('@modules/auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    canActivate: [customerGuard],
    path: 'purchase/:id',
    loadComponent: () => import('@app/modules/purchase/pages/create-purchase/create-purchase.component').then(m => m.PurchaseComponent)
  },
  {
    path: '**',
    redirectTo: 'home'
  }
];
