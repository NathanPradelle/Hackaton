import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
    selector: 'app-home',
    imports: [],
    templateUrl: './home.html',
    styleUrl: './home.css',
})
export class Home {
    constructor(private router: Router) {}

    goToPreviousOrders() {
        this.router.navigate(['']); //TODO navigate to previous orders
    }

    goToNewOrder() {
        this.router.navigate(['']); //TODO navigate to new order page
    }
}
