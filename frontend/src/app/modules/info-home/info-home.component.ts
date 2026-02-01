import { Component } from '@angular/core';
import { HomeComponent } from '../home/home.component';
import { NavBarComponent } from '../nav-bar/nav-bar.component';

@Component({
  selector: 'app-info-home',
  standalone: true,
  imports: [NavBarComponent, HomeComponent],
  templateUrl: './info-home.component.html',
  styleUrl: './info-home.component.css'
})
export class InfoHomeComponent {

}
