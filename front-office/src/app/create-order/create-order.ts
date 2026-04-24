import { CommonModule, CurrencyPipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnDestroy, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

interface PriceBreakdown {
  distanceKm: number;
  coutCarburant: number;
  partSalariale: number;
  usureCamion: number;
  badgeTelepeage: number;
  fraisLivraison: number;
  sousTotal: number;
  marge: number;
  prixTotal: number;
}

type ToastType = 'success' | 'error' | 'info';
interface ToastMessage { id: number; type: ToastType; title: string; message: string; }

@Component({
  selector: 'app-create-order',
  imports: [CommonModule, ReactiveFormsModule, CurrencyPipe],
  templateUrl: './create-order.html',
  styleUrl: './create-order.css',
})
export class CreateOrder implements OnDestroy {
  orderForm: FormGroup;
  submitted = false;

  loading   = signal(false);
  step      = signal<'form' | 'confirm'>('form');
  breakdown = signal<PriceBreakdown | null>(null);
  toasts    = signal<ToastMessage[]>([]);

  private nextToastId = 1;
  private toastTimers: number[] = [];

  private readonly apiUrl = 'http://localhost:8080/api/orders';

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private http: HttpClient,
    private authService: AuthService
  ) {
    this.orderForm = this.fb.group({
      address:       ['', [Validators.required, Validators.minLength(5)]],
      requestedDate: ['', [Validators.required]],
      timeSlot:      ['', [Validators.required]],
      quantity:      [1,  [Validators.required, Validators.min(1)]],
    });
  }

  isControlInvalid(name: string): boolean {
    const c = this.orderForm.get(name);
    return !!c && c.invalid && (c.touched || this.submitted);
  }

  simulatePrice() {
    this.submitted = true;
    if (this.orderForm.invalid) {
      this.orderForm.markAllAsTouched();
      this.addToast('error', 'Formulaire incomplet', 'Vérifiez tous les champs avant de continuer.');
      return;
    }

    this.loading.set(true);
    const user = this.authService.getCurrentUser();
    const payload = {
      adresseTexte: this.orderForm.value.address,
      dateVoulu:    this.orderForm.value.requestedDate,
      plageHoraire: this.orderForm.value.timeSlot,
      quantite:     this.orderForm.value.quantity,
      clientId:     user?.clientId ?? null,
    };

    this.http.post<PriceBreakdown>(`${this.apiUrl}/simulate`, payload).subscribe({
      next: (data) => {
        this.loading.set(false);
        this.breakdown.set(data);
        this.step.set('confirm');
      },
      error: () => {
        this.loading.set(false);
        this.addToast('error', 'Erreur de simulation', 'Impossible de calculer le prix. Réessayez.');
      }
    });
  }

  confirmOrder() {
    this.loading.set(true);
    const user = this.authService.getCurrentUser();
    const payload = {
      adresseTexte: this.orderForm.value.address,
      dateVoulu:    this.orderForm.value.requestedDate,
      plageHoraire: this.orderForm.value.timeSlot,
      quantite:     this.orderForm.value.quantity,
      clientId:     user?.clientId ?? null,
      prix:         this.breakdown()?.prixTotal ?? 0,
    };

    this.http.post(`${this.apiUrl}`, payload).subscribe({
      next: () => {
        this.loading.set(false);
        this.addToast('success', 'Commande confirmée', 'Votre commande a bien été enregistrée.');
        setTimeout(() => this.router.navigate(['/orders/previous']), 1500);
      },
      error: () => {
        this.loading.set(false);
        this.addToast('error', 'Erreur', 'La commande n\'a pas pu être enregistrée.');
      }
    });
  }

  backToForm() {
    this.step.set('form');
    this.breakdown.set(null);
  }

  addToast(type: ToastType, title: string, message: string) {
    const toast: ToastMessage = { id: this.nextToastId++, type, title, message };
    this.toasts.update(t => [...t, toast]);
    const t = window.setTimeout(() => this.removeToast(toast.id), 3500);
    this.toastTimers.push(t);
  }

  removeToast(id: number) {
    this.toasts.update(t => t.filter(x => x.id !== id));
  }

  goBack() { this.router.navigate(['/home']); }

  ngOnDestroy() {
    this.toastTimers.forEach(t => window.clearTimeout(t));
  }
}
