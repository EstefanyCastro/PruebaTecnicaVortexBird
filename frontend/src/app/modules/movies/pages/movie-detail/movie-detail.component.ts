import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-movie-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div style="padding: 2rem; text-align: center;">
      <h2>Detalle de Película</h2>
      <p>Esta sección está en desarrollo</p>
      <a routerLink="/movies/list" style="color: #667eea;">Volver a la lista</a>
    </div>
  `
})
export class MovieDetailComponent {}
