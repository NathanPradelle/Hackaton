import { Component, signal, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.component.html'
})
export class LoginComponent {
  identifiant = signal('');
  password = signal('');

  private router = inject(Router);
  private authService = inject(AuthService);

  login() {
    // Simulation d'une connexion d'admin
    if (this.identifiant() && this.password()) {
      this.authService.login(); // Met à jour l'état à "connecté"
      this.router.navigate(['/dashboard']);
    }
  }
}