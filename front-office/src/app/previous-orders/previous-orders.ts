import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

interface Order {
    id: number;
    deliveryDate: string;
    price: number;
    quantity: number;
    status: 'PENDING' | 'DELIVERED' | 'CANCELLED';
}

type SortField = 'id' | 'deliveryDate' | 'price' | 'quantity' | 'status';
type SortDirection = 'asc' | 'desc';

@Component({
    selector: 'app-previous-orders',
    imports: [CommonModule, FormsModule],
    templateUrl: './previous-orders.html',
    styleUrl: './previous-orders.css',
})
export class PreviousOrders {
    orders: Order[] = [
        {
            id: 1001,
            deliveryDate: '2026-04-24',
            price: 120.50,
            quantity: 5,
            status: 'PENDING'
        },
        {
            id: 1002,
            deliveryDate: '2026-04-24',
            price: 85.00,
            quantity: 2,
            status: 'DELIVERED'
        },
        {
            id: 1003,
            deliveryDate: '2026-04-24',
            price: 210.00,
            quantity: 8,
            status: 'CANCELLED'
        }
    ];

    searchTerm = '';
    sortField: SortField = 'deliveryDate';
    sortDirection: SortDirection = 'desc';

    constructor(private router: Router) {}

    get hasOrders(): boolean {
        return this.orders.length > 0;
    }

    get displayedOrders(): Order[] {
        const normalizedTerm = this.searchTerm.trim().toLowerCase();

        const filteredOrders = this.orders.filter((order) => {
            if (!normalizedTerm) {
                return true;
            }

            const statusFr = this.getStatusLabel(order.status).toLowerCase();
            return (
                order.id.toString().includes(normalizedTerm) ||
                order.deliveryDate.toLowerCase().includes(normalizedTerm) ||
                order.status.toLowerCase().includes(normalizedTerm) ||
                statusFr.includes(normalizedTerm)
            );
        });

        return [...filteredOrders].sort((a, b) => this.compareOrders(a, b));
    }

    get hasFilteredResults(): boolean {
        return this.displayedOrders.length > 0;
    }

    getStatusLabel(status: Order['status']): string {
        if (status === 'PENDING') {
            return 'En attente';
        }
        if (status === 'DELIVERED') {
            return 'Livree';
        }
        return 'Annulee';
    }

    setSort(field: SortField) {
        if (this.sortField === field) {
            this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
            return;
        }

        this.sortField = field;
        this.sortDirection = 'asc';
    }

    isSorted(field: SortField): boolean {
        return this.sortField === field;
    }

    private compareOrders(a: Order, b: Order): number {
        const result =
            this.sortField === 'id' || this.sortField === 'price' || this.sortField === 'quantity'
                ? a[this.sortField] - b[this.sortField]
                : a[this.sortField].localeCompare(b[this.sortField]);

        return this.sortDirection === 'asc' ? result : -result;
    }

    goBack() {
        this.router.navigate(['/home']);
    }
}
