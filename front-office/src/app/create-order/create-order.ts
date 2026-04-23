import { CommonModule } from '@angular/common';
import { Component, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

type ToastType = 'success' | 'error' | 'info';

interface ToastMessage {
    id: number;
    type: ToastType;
    title: string;
    message: string;
}

@Component({
    selector: 'app-create-order',
    imports: [CommonModule, ReactiveFormsModule],
    templateUrl: './create-order.html',
    styleUrl: './create-order.css',
})
export class CreateOrder implements OnDestroy {
    orderForm: FormGroup;
    submitted = false;
    loading = false;
    toasts: ToastMessage[] = [];

    private nextToastId = 1;
    private toastTimers: number[] = [];

    constructor(private fb: FormBuilder, private router: Router) {
        this.orderForm = this.fb.group({
            addressText: ['', [Validators.required, Validators.minLength(5)]],
            requestedDate: ['', [Validators.required]],
            timeSlot: ['', [Validators.required]],
            price: ['', [Validators.required, Validators.min(0)]],
            quantity: [1, [Validators.required, Validators.min(1)]],
        });
    }

    isControlInvalid(controlName: string): boolean {
        const control = this.orderForm.get(controlName);
        return !!control && control.invalid && (control.touched || this.submitted);
    }

    onSubmit() {
        this.submitted = true;

        if (this.orderForm.invalid) {
            this.orderForm.markAllAsTouched();
            this.addToast('error', 'Formulaire incomplet', 'Verifiez les champs obligatoires avant envoi.');
            return;
        }

        this.loading = true;

        // Simule l'envoi API en attendant le branchement backend commande.
        setTimeout(() => {
            this.loading = false;
            const orderNumber = `CMD-${Date.now().toString().slice(-6)}`;
            this.addToast('success', 'Commande envoyee', `Votre commande ${orderNumber} a bien ete enregistree.`);
            this.orderForm.reset({ quantity: 1 });
            this.submitted = false;
        }, 900);
    }

    addToast(type: ToastType, title: string, message: string) {
        const toast: ToastMessage = {
            id: this.nextToastId++,
            type,
            title,
            message,
        };

        this.toasts = [...this.toasts, toast];
        const timerId = window.setTimeout(() => this.removeToast(toast.id), 3500);
        this.toastTimers.push(timerId);
    }

    removeToast(id: number) {
        this.toasts = this.toasts.filter((toast) => toast.id !== id);
    }

    goBack() {
        this.router.navigate(['/home']);
    }

    ngOnDestroy() {
        this.toastTimers.forEach((timerId) => window.clearTimeout(timerId));
        this.toastTimers = [];
    }
}
