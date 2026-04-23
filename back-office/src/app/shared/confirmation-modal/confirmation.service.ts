import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ConfirmationService {
  private state = signal<{ message: string; onConfirm: () => void } | null>(null);

  readonly confirmationState = this.state.asReadonly();

  show(message: string, onConfirm: () => void) {
    this.state.set({ message, onConfirm });
  }

  hide() {
    this.state.set(null);
  }

  confirm() {
    this.state()?.onConfirm();
    this.hide();
  }
}
