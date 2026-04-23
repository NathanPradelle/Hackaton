import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AuthService {
  isAuthenticated = signal<boolean>(localStorage.getItem('isLoggedIn') === 'true');

  login() {
    this.isAuthenticated.set(true);
    localStorage.setItem('isLoggedIn', 'true');
  }

  logout() {
    this.isAuthenticated.set(false);
    localStorage.removeItem('isLoggedIn');
  }
}