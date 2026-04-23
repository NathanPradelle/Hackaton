import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ToastService {
  message = signal('');
  type = signal<'success' | 'error'>('success');

  show(msg: string, t: 'success' | 'error' = 'success') {
    this.message.set(msg);
    this.type.set(t);
    setTimeout(() => {
      this.message.set('');
    }, 3000); // Disparaît au bout de 3 secondes
  }
}