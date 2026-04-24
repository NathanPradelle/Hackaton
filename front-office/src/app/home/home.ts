import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
    selector: 'app-home',
    templateUrl: './home.html',
    styleUrl: './home.css',
})
export class Home {
    constructor(
        private router: Router,
        private authService: AuthService
    ) {}

    goToPreviousOrders() {
        this.router.navigate(['/orders/previous']);
    }

    goToNewOrder() {
        this.router.navigate(['/orders/new']);
    }

    logout() {
        this.authService.logout();
        this.router.navigate(['/auth']);
    }
}
