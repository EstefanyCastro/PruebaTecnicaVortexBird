import { Routes } from '@angular/router';
import { adminGuard } from '@core/guards/admin.guard';

/**
 * Movies module routes.
 * 
 * Contains routes for browsing, creating, and viewing movies.
 * Admin routes are protected with adminGuard.
 */
export const MOVIES_ROUTES: Routes = [
  {
    path: 'manage',
    canActivate: [adminGuard],
    loadComponent: () => import('./pages/movie-manage/movie-manage.component').then(m => m.MovieManageComponent)
  },
  {
    path: 'manage/create',
    canActivate: [adminGuard],
    loadComponent: () => import('./pages/create-movie/create-movie.component').then(m => m.CreateMovieComponent)
  },
  {
    path: 'manage/edit/:id',
    canActivate: [adminGuard],
    loadComponent: () => import('./pages/create-movie/create-movie.component').then(m => m.CreateMovieComponent)
  },
  {
    path: ':id',
    loadComponent: () => import('./pages/movie-detail/movie-detail.component').then(m => m.MovieDetailComponent)
  },
  {
    path: '',
    redirectTo: 'manage',
    pathMatch: 'full'
  }
];
