import { Routes } from '@angular/router';
import { adminGuard } from '@core/guards/admin.guard';

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
    loadComponent: () => import('@modules/info-home/info-home.component').then(m => m.InfoHomeComponent)
  },
  {
    path: 'movies',
    loadChildren: () => import('@modules/movies/movies.routes').then(m => m.MOVIES_ROUTES)
  },
  {
    path: 'customers',
    canActivate: [adminGuard],
    loadComponent: () => import('@modules/customer/customer-manage/customer-manage.component').then(m => m.CustomerManageComponent)
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
    path: '**',
    redirectTo: 'home'
  }
];
