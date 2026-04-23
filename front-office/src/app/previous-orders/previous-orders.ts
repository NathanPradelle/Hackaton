import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
    selector: 'app-previous-orders',
    imports: [],
    templateUrl: './previous-orders.html',
    styleUrl: './previous-orders.css',
})
export class PreviousOrders {
    constructor(private router: Router) {}

    goBack() {
        this.router.navigate(['/home']);
    }
}
