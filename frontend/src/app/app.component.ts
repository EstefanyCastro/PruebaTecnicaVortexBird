import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

/**
 * Root component of the application.
 * 
 * Serves as the main container for the application layout and routing.
 */
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  template: `<router-outlet></router-outlet>`,
  styles: []
})
export class AppComponent {
  title = 'Movie Ticket Booking System';
}
