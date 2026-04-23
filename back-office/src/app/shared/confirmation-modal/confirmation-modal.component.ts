import { Component, inject } from '@angular/core';
import { ConfirmationService } from './confirmation.service';

@Component({
  selector: 'app-confirmation-modal',
  standalone: true,
  template: `
    @if (confirmationService.confirmationState(); as state) {
      <div class="modal-overlay">
        <div class="modal-content" style="max-width: 400px;">
          <h3 style="margin-top: 0;">Confirmation</h3>
          <p>{{ state.message }}</p>
          <div style="display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px;">
            <button type="button" (click)="confirmationService.hide()" style="background: #e2e8f0; color: #4a5568; width: auto;">Annuler</button>
            <button type="button" (click)="confirmationService.confirm()" class="danger" style="width: auto;">Confirmer</button>
          </div>
        </div>
      </div>
    }
  `,
})
export class ConfirmationModalComponent {
  confirmationService = inject(ConfirmationService);
}
