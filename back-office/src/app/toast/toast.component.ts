import { Component, inject } from '@angular/core';
import { ToastService } from './toast.service';

@Component({
  selector: 'app-toast',
  template: `
    @if (toastService.message()) {
      <div class="toast" [class.success]="toastService.type() === 'success'" [class.error]="toastService.type() === 'error'">
        {{ toastService.message() }}
      </div>
    }
  `,
  styles: [`
    .toast {
      position: fixed;
      bottom: 20px;
      right: 20px;
      padding: 15px 25px;
      border-radius: 8px;
      color: white;
      font-weight: 500;
      box-shadow: 0 4px 12px rgba(0,0,0,0.15);
      z-index: 1000;
      animation: slideIn 0.3s ease-out;
    }
    .success { background-color: #48bb78; }
    .error { background-color: #e53e3e; }
    @keyframes slideIn { from { transform: translateX(100%); opacity: 0; } to { transform: translateX(0); opacity: 1; } }
  `]
})
export class ToastComponent {
  toastService = inject(ToastService);
}