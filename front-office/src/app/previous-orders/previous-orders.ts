import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../services/auth.service';

interface Order {
  id: number;
  adresseTexte: string;
  dateVoulu: string;
  plageHoraire: string;
  prix: number;
  quantite: number;
  statut: 'enAttente' | 'confirmee' | 'enCours' | 'livree' | 'annulee';
}

type SortField = 'id' | 'dateVoulu' | 'prix' | 'quantite' | 'statut';

@Component({
  selector: 'app-previous-orders',
  imports: [CommonModule, FormsModule],
  templateUrl: './previous-orders.html',
  styleUrl: './previous-orders.css',
})
export class PreviousOrders implements OnInit {
  orders: Order[] = [];
  loading = true;
  error = false;

  searchTerm = '';
  sortField: SortField = 'dateVoulu';
  sortDirection: 'asc' | 'desc' = 'desc';

  private readonly apiUrl = 'http://localhost:8080/api/orders';

  constructor(
    private router: Router,
    private http: HttpClient,
    private authService: AuthService
  ) {}

  ngOnInit() {
    const user = this.authService.getCurrentUser();
    const clientId = user?.clientId;

    if (!clientId) {
      this.loading = false;
      return;
    }

    this.http.get<Order[]>(`${this.apiUrl}/client/${clientId}`).subscribe({
      next: (data) => { this.orders = data; this.loading = false; },
      error: () => { this.error = true; this.loading = false; }
    });
  }

  get displayedOrders(): Order[] {
    const term = this.searchTerm.trim().toLowerCase();
    const filtered = this.orders.filter(o =>
      !term ||
      o.id.toString().includes(term) ||
      o.dateVoulu.includes(term) ||
      o.adresseTexte?.toLowerCase().includes(term) ||
      this.getStatusLabel(o.statut).toLowerCase().includes(term)
    );
    return [...filtered].sort((a, b) => this.compareOrders(a, b));
  }

  get hasOrders(): boolean { return this.orders.length > 0; }
  get hasFilteredResults(): boolean { return this.displayedOrders.length > 0; }

  getStatusLabel(statut: Order['statut']): string {
    const labels: Record<string, string> = {
      enAttente: 'En attente',
      confirmee: 'Confirmée',
      enCours:   'En cours',
      livree:    'Livrée',
      annulee:   'Annulée',
    };
    return labels[statut] ?? statut;
  }

  getStatusClass(statut: Order['statut']): string {
    const classes: Record<string, string> = {
      enAttente: 'status-pending',
      confirmee: 'status-confirmed',
      enCours:   'status-in-progress',
      livree:    'status-delivered',
      annulee:   'status-cancelled',
    };
    return classes[statut] ?? '';
  }

  setSort(field: SortField) {
    if (this.sortField === field) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortField = field;
      this.sortDirection = 'asc';
    }
  }

  isSorted(field: SortField): boolean { return this.sortField === field; }

  private compareOrders(a: Order, b: Order): number {
    let result: number;
    if (this.sortField === 'id' || this.sortField === 'prix' || this.sortField === 'quantite') {
      result = (a[this.sortField] as number) - (b[this.sortField] as number);
    } else {
      result = (a[this.sortField] as string).localeCompare(b[this.sortField] as string);
    }
    return this.sortDirection === 'asc' ? result : -result;
  }

  goBack() { this.router.navigate(['/home']); }
}
