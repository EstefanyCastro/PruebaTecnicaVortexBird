import { Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth.guard';

/**
 * Movies module routes.
 * 
 * Contains routes for browsing, creating, and viewing movies.
 */
export const MOVIES_ROUTES: Routes = [
  {
    path: 'list',
    loadComponent: () => import('./pages/movie-list/movie-list.component').then(m => m.MovieListComponent)
  },
  {
    path: 'create',
    loadComponent: () => import('./pages/create-movie/create-movie.component').then(m => m.CreateMovieComponent)
  },
  {
    path: ':id',
    loadComponent: () => import('./pages/movie-detail/movie-detail.component').then(m => m.MovieDetailComponent)
  },
  {
    path: '',
    redirectTo: 'list',
    pathMatch: 'full'
  }
];
