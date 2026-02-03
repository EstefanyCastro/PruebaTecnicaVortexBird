import { Component } from '@angular/core';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { filter } from 'rxjs/operators';
import { NavBarComponent } from '@modules/nav-bar/nav-bar.component';

/**
 * Root component of the application.
 * 
 * Serves as the main container for the application layout and routing.
 */
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, NavBarComponent],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'Movie Ticket Booking System';
  showNavBar = true;

  constructor(private router: Router) {
    // Hide navbar on auth pages
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      this.showNavBar = !event.url.includes('/auth/');
    });
  }
}
